package com.urwork.mobile.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.urwork.mobile.DetailProject
import com.urwork.mobile.R
import com.urwork.mobile.models.ProjectModelData

class Project1(
    private val ctx: Context,
    private var topTitleisProgress: Boolean = false,
    private var mList: ArrayList<ProjectModelData>
) : RecyclerView.Adapter<Project1.ViewHolder>() {

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
            .inflate(R.layout.item_project_1, parent, false)

        return ViewHolder(view, mListener)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = mList[position]

        val tagAdapter = item.tags?.let { Tag(ctx, it.split(", ")) }

        holder.toptitle_tv.text =
            if (topTitleisProgress) "On progress" else "${item.author?.firstName} and ${item.collaborators?.size} collaborators"
        holder.title_tv.text = item.title
        holder.subtitle_tv.text = item.description
        holder.tags.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
        holder.tags.adapter = tagAdapter

        tagAdapter?.setOnItemClickListener(object : Tag.onItemClickListener {
            override fun onItemClick(position: Int) {
                Log.i("SKIP", "NOTHING")
            }
        })

        holder.parentView.setOnClickListener {
            val intent = Intent(ctx, DetailProject::class.java)
            intent.putExtra("PROJECT_ID", item.Id)

            ctx.startActivity(intent)
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View, listener: onItemClickListener) :
        RecyclerView.ViewHolder(ItemView) {
        val parentView: View = ItemView
        val toptitle_tv: TextView = itemView.findViewById(R.id.item_project_toptitle)
        val title_tv: TextView = itemView.findViewById(R.id.item_project_title)
        val subtitle_tv: TextView = itemView.findViewById(R.id.item_project_subtitle)
        val tags: RecyclerView = itemView.findViewById(R.id.item_project_tags)

        init {
            listener?.let { l ->
                itemView.setOnClickListener {
                    l.onItemClick(adapterPosition)
                }
            }
        }
    }
}