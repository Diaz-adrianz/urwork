package com.urwork.mobile.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.urwork.mobile.R
import com.urwork.mobile.api.ApiBuilder
import com.urwork.mobile.api.TaskApi
import com.urwork.mobile.models.TaskModelData
import com.urwork.mobile.services.ApiEnqueue
import com.urwork.mobile.services.TinyDB
import com.urwork.mobile.services.formatDate
import okhttp3.internal.concurrent.Task

class TaskAdapter(
    private val ctx: Context,
    private var subtitleIsBy: Boolean = false,
    private var mList: ArrayList<TaskModelData>,
    private val clickToDelete: Boolean = false,
    private val clickToUpdate: Boolean = true
) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    lateinit var prefs: TinyDB
    lateinit var taskServ: TaskApi
    lateinit var loaderr: ProgressBar
    lateinit var bs: BottomSheetDialog
    private lateinit var mListener: onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filterlist: ArrayList<TaskModelData>) {
        mList = filterlist
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return ViewHolder(view, mListener)
    }

    @SuppressLint("SetTextI18n", "ResourceAsColor", "MissingInflatedId")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: TaskModelData = mList[position]

        holder.title.text = item.title.toString()
        holder.subtitle.text =
            if (subtitleIsBy) "Completed by ${if (item.completedBy?.firstName == null) "-" else item.completedBy?.firstName}" else "Project: ${item.projectId?.title}"

        if (item.completedDate != null) {
            holder.title.setTextColor(ContextCompat.getColor(ctx, R.color.gray))
            holder.statusIcon.setImageResource(R.drawable.ic_round_check_box_24)
        } else {
            holder.title.setTextColor(ContextCompat.getColor(ctx, R.color.black))
            holder.statusIcon.setImageResource(R.drawable.ic_round_check_box_outline_blank_24)
        }

        holder.delIcon.isVisible = clickToDelete && !clickToUpdate

        if (!clickToDelete && clickToUpdate) {
            holder.parentView.setOnClickListener {
                val v: View =
                    LayoutInflater.from(ctx).inflate(R.layout.bottom_sheet_task, null, false)
                bs = BottomSheetDialog(ctx)

                bs.setContentView(v)
                val title_tv: TextView = v.findViewById(R.id.title)
                val message_tv: TextView = v.findViewById(R.id.message)
                val positive_btn: Button = v.findViewById(R.id.positive_btn)
                val negative_btn: Button = v.findViewById(R.id.negative_btn)
                loaderr = v.findViewById(R.id.loaderr)
                loaderr.isVisible = false

                title_tv.text = if (item.completedDate != null) "Task info" else "Are you sure?"
                message_tv.text =
                    if (item.completedDate != null) "Completed by '${item.completedBy?.firstName}' at ${
                        formatDate(
                            item.completedDate!!,
                            "dd MMMM yyyy"
                        )
                    }" else "you will complete the task '${item.title}' in the '${item.projectId?.title}' project. Remember this action will cannot be performed again."

                bs.setOnShowListener { dialog ->
                    positive_btn.isVisible = item.completedDate == null
                }

                positive_btn.setOnClickListener {
                    if (item.completedDate == null) {
                        completeTask(position)
                    } else {
                        bs.dismiss()
                    }
                }

                negative_btn.setOnClickListener {
                    bs.dismiss()
                }

//            if (item.completedDate != null || item.isMine == false) bs.show()
////
                if (item.completedDate == null) {
                    if (item.isMine == true) bs.show()
                } else {
                    bs.show()
                }
            }
        }
    }

    class ViewHolder(ItemView: View, listener: onItemClickListener) :
        RecyclerView.ViewHolder(ItemView) {
        val parentView: View = ItemView
        val statusIcon: ImageView = itemView.findViewById(R.id.task_status_icon)
        val title: TextView = itemView.findViewById(R.id.task_title)
        val subtitle: TextView = itemView.findViewById(R.id.task_subtitle)
        val delIcon: ImageView = ItemView.findViewById(R.id.task_del_icon)

        init {
            listener?.let { l ->
                itemView.setOnClickListener {
                    l.onItemClick(adapterPosition)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun completeTask(pos: Int) {
        prefs = TinyDB(ctx)
        taskServ = ApiBuilder.buildService(
            TaskApi::class.java,
            prefs.getString(R.string.tokenname.toString())
        )

        loaderr.isVisible = true

        ApiEnqueue.enqueue(
            ctx,
            taskServ.completeTask(mList[pos].projectId?.Id, mList[pos].Id)
        ) { res, code, err ->
            if (res != null && code == 200) {
                val mList2: ArrayList<TaskModelData> = mList

                mList2.removeAt(pos)
                filterList(mList2)
            }

            loaderr.isVisible = false
            bs.dismiss()
        }
    }
}