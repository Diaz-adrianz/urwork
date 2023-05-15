package com.urwork.mobile.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.urwork.mobile.Profile
import com.urwork.mobile.R
import com.urwork.mobile.models.*
import com.urwork.mobile.services.formatDate

class NotifAdapter (private  val ctx: Context, private var mList: ArrayList<NotifModelData>) : RecyclerView.Adapter<NotifAdapter.ViewHolder>() {

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position : Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filterlist: ArrayList<NotifModelData>) {
        mList = filterlist
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_common_simple, parent, false)
        return ViewHolder(view, mListener)
    }

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: NotifModelData = mList[position]

        holder.title.text = item.title
        holder.subtitle.text = item.message + " • " + item.createdAt?.let { formatDate(it, "dd MMMM yyyy") }

        holder.image.setImageResource(R.drawable.ic_round_mark_email_unread_24)
        holder.image.imageTintList = ContextCompat.getColorStateList(ctx, R.color.primary)

        if (item.isRead!!) {
            holder.image.imageTintList = ContextCompat.getColorStateList(ctx, R.color.gray)
            holder.title.setTextColor(ContextCompat.getColorStateList(ctx, R.color.gray))
        }
    }

    class ViewHolder(ItemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(ItemView) {
        val parentView: View = itemView
        val image: ImageView = itemView.findViewById(R.id.item_image)
        val title: TextView = itemView.findViewById(R.id.item_title)
        val subtitle: TextView = itemView.findViewById(R.id.item_subtitle)

        init {
            listener?.let { l ->
                itemView.setOnClickListener{
                    l.onItemClick(adapterPosition)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}