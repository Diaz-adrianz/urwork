package com.urwork.mobile.adapters

import android.annotation.SuppressLint
import android.content.ClipData.Item
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.urwork.mobile.R
import com.urwork.mobile.models.ProjectModelData
import com.urwork.mobile.models.UserModelData
import com.urwork.mobile.services.formatDate
import org.w3c.dom.Text

class UserPhotoAdapter(
    private val ctx: Context,
    private val withXbtn: Boolean = false,
    private var mList: ArrayList<UserModelData>
) : RecyclerView.Adapter<UserPhotoAdapter.ViewHolder>() {

    private lateinit var mListener: onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filterlist: ArrayList<UserModelData>) {
        mList = filterlist
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteItem(i: Int) {
        mList.removeAt(i)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addItem(i: Int, news: UserModelData) {
        mList.add(i, news)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_photo, parent, false)

        return ViewHolder(view, mListener)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mList[position]

        Glide
            .with(ctx)
            .load(item.photo)
            .placeholder(R.drawable.loading)
            .error(R.drawable.blank_profilepic)
            .centerCrop()
            .into(holder.photo);
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View, listener: onItemClickListener) :
        RecyclerView.ViewHolder(ItemView) {
        val photo: ImageView = itemView.findViewById(R.id.profil_photo)

        init {
            listener?.let { l ->
                itemView.setOnClickListener {
                    l.onItemClick(adapterPosition)
                }
            }
        }
    }
}