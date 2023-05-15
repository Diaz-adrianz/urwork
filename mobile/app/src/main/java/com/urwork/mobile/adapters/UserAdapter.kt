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
import com.urwork.mobile.models.ProjectModelData
import com.urwork.mobile.models.TaskModel
import com.urwork.mobile.models.TaskModelData
import com.urwork.mobile.models.UserModelData

class UserAdapter (private  val ctx: Context, private var mList: ArrayList<UserModelData>) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position : Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filterlist: ArrayList<UserModelData>) {
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
        val item: UserModelData = mList[position]

        holder.title.text = item.firstName + " " + item.lastName
        holder.subtitle.text = item.about

        Glide
            .with(ctx)
            .load(item.photo)
            .placeholder(R.drawable.loading)
            .error(R.drawable.blank_profilepic)
            .centerCrop()
            .into(holder.image);

//        NEED TEST!!!
        holder.parentView.setOnClickListener {
            val intent = Intent(ctx, Profile::class.java)
            intent.putExtra("USER_ID", item.Id)

            ctx.startActivity(intent)
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