package com.urwork.mobile.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.view.marginRight
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.urwork.mobile.R
import com.urwork.mobile.models.UserModelData

class ImageListAdapter(
    private val ctx: Context,
    private var mList: List<String>,
    private var withXbtn: Boolean = false,
    private var errImage: Int = R.drawable.ic_round_broken_image_24
) : RecyclerView.Adapter<ImageListAdapter.ViewHolder>() {

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
            .inflate(R.layout.item_image, parent, false)
        return ViewHolder(view, mListener)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Glide
            .with(ctx)
            .load(mList[position])
            .placeholder(R.drawable.loading)
            .error(errImage)
            .centerCrop()
            .into(holder.iv);

        if (position % 2 == 0) {
            val layoutParams = holder.parentView.layoutParams as ViewGroup.MarginLayoutParams

            layoutParams.setMargins(8, 0, 8, 8)

            holder.parentView.layoutParams = layoutParams
        }
        holder.xBtn.isVisible = withXbtn
    }

    class ViewHolder(ItemView: View, listener: onItemClickListener) :
        RecyclerView.ViewHolder(ItemView) {
        val parentView: View = itemView
        val iv: ImageView = itemView.findViewById(R.id.image)
        val xBtn: TextView = itemView.findViewById(R.id.x_btn)

        init {
            listener?.let { l ->
                xBtn.setOnClickListener {
                    l.onItemClick(adapterPosition)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}