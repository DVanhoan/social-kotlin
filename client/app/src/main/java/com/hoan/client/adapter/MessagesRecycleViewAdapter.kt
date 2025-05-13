package com.hoan.client.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hoan.client.R
import com.hoan.client.network.response.RecentMessages
import com.squareup.picasso.Picasso

class MessagesRecycleViewAdapter(
    private val context: Context,
    private var messages: List<RecentMessages>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_LEFT = 0
        private const val VIEW_TYPE_RIGHT = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isSender) VIEW_TYPE_RIGHT
        else VIEW_TYPE_LEFT
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_RIGHT) {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.list_item_message_sent, parent, false)
            RightMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.list_item_message_received, parent, false)
            LeftMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is RightMessageViewHolder) {
            holder.bind(message)
        } else if (holder is LeftMessageViewHolder) {
            holder.bind(message)
        }
    }

    override fun getItemCount(): Int = messages.size

    fun updateMessages(newMessages: List<RecentMessages>) {
        messages = newMessages
        notifyDataSetChanged()
    }

    inner class LeftMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMessage: TextView = itemView.findViewById(R.id.tv_message)
        private val ivImage: ImageView = itemView.findViewById(R.id.iv_message_image)
        private val ivProfile: ImageView = itemView.findViewById(R.id.iv_profile_picture)
        fun bind(message: RecentMessages) {
            // text
            if (!message.content.isNullOrBlank()) {
                tvMessage.visibility = View.VISIBLE
                tvMessage.text = message.content
            } else {
                tvMessage.visibility = View.GONE
            }

            // image
            if (message.image_url.toString() != "null") {
                ivImage.visibility = View.VISIBLE
                Picasso.get()
                    .load(message.image_url)
                    .into(ivImage)
            } else {
                Picasso.get().cancelRequest(ivImage)
                ivImage.setImageDrawable(null)
                ivImage.visibility = View.GONE
            }


            Picasso.get()
                .load(message.sender.profilePicture)
                .placeholder(R.drawable.profile_placeholder)
                .into(ivProfile)
        }
    }

    inner class RightMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMessage: TextView = itemView.findViewById(R.id.tv_message)
        private val ivImage: ImageView = itemView.findViewById(R.id.iv_message_image)
        private val ivProfile: ImageView = itemView.findViewById(R.id.iv_profile_picture)
        fun bind(message: RecentMessages) {
            // text
            if (!message.content.isNullOrBlank()) {
                tvMessage.visibility = View.VISIBLE
                tvMessage.text = message.content
            } else {
                tvMessage.visibility = View.GONE
            }

            // image
            if (message.image_url.toString() != "null") {
                ivImage.visibility = View.VISIBLE
                Picasso.get()
                    .load(message.image_url)
                    .into(ivImage)
            } else {
                Picasso.get().cancelRequest(ivImage)
                ivImage.setImageDrawable(null)
                ivImage.visibility = View.GONE
            }
            Picasso.get()
                .load(message.sender.profilePicture)
                .placeholder(R.drawable.profile_placeholder)
                .into(ivProfile)
        }
    }
}
