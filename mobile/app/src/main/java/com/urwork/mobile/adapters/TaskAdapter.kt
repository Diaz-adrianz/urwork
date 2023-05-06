package com.urwork.mobile.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.urwork.mobile.R
import com.urwork.mobile.models.ProjectModelData
import com.urwork.mobile.models.TaskModel
import com.urwork.mobile.models.TaskModelData

class TaskAdapter (private  val ctx: Context, private  var subtitleIsBy: Boolean = false, private var mList: ArrayList<TaskModelData>) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filterlist: ArrayList<TaskModelData>) {
        mList = filterlist
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: TaskModelData = mList[position]

        holder.title.text = item.title.toString()
        holder.subtitle.text = if (subtitleIsBy) "Completed by ${item.completedBy?.firstName}" else "Proyek: ${item.projectId?.title}"

        if (item.completedDate != "") {
            holder.title.setTextColor(R.color.silver)
            holder.subtitle.setTextColor(R.color.silver)
            holder.statusIcon.setColorFilter(R.color.gray)
            holder.statusIcon.setImageResource(R.drawable.ic_round_check_circle_24)
        }
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val statusIcon: ImageView = itemView.findViewById(R.id.task_status_icon)
        val title: TextView = itemView.findViewById(R.id.task_title)
        val subtitle: TextView = itemView.findViewById(R.id.task_subtitle)
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}