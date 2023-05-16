package com.urwork.mobile.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.urwork.mobile.R
import com.urwork.mobile.models.UserModelData

class Tag(private val ctx: Context, private var mList: List<String>) : RecyclerView.Adapter<Tag.ViewHolder>() {

    private lateinit var mListener: onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filterlist: List<String>) {
        mList = filterlist
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tag, parent, false)
        return ViewHolder(view, mListener)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv.text = mList[position]
    }

    class ViewHolder(ItemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(ItemView) {
        val tv: TextView = itemView.findViewById(R.id.text)

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
}