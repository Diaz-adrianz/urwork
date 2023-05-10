package com.urwork.mobile.adapters

import android.annotation.SuppressLint
import android.content.ClipData.Item
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.urwork.mobile.R
import com.urwork.mobile.models.ProjectModelData
import com.urwork.mobile.services.formatDate
import org.w3c.dom.Text

class ProjectAdapter(
    private val ctx: Context,
    private val layout: Int,
    private var mList: ArrayList<ProjectModelData>
) : RecyclerView.Adapter<ProjectAdapter.ViewHolder>() {

    private lateinit var mListener: onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filterlist: ArrayList<ProjectModelData>) {
        mList = filterlist
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteItem(i: Int) {
        mList.removeAt(i)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addItem(i: Int, news: ProjectModelData) {
        mList.add(i, news)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(layout, parent, false)

        return ViewHolder(view, mListener)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mList[position]

        holder.title.text = item.title
        holder.subtitle.text =
            "Deadline: " + formatDate(item.durationEnd.toString(), "dd MMMM YYYY")
        holder.progressBar.progress = if(item.percentage != null) item.percentage!! else 0
        holder.collabs.text = "${item.collaborators?.size} collaborators"
        holder.percentage.text = "${if (item.percentage != null) item.percentage else 0}%"
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View, listener: onItemClickListener) :
        RecyclerView.ViewHolder(ItemView) {
        val title: TextView = ItemView.findViewById(R.id.item_project_title)
        val subtitle: TextView = ItemView.findViewById(R.id.item_project_subtitle)
        val progressBar: ProgressBar = ItemView.findViewById(R.id.item_project_progress)
        val collabs: TextView = ItemView.findViewById(R.id.item_project_collabs)
        val percentage: TextView = ItemView.findViewById(R.id.item_project_percen)

        init {
            listener?.let { l ->
                itemView.setOnClickListener {
                    l.onItemClick(adapterPosition)
                }
            }
        }
    }
}