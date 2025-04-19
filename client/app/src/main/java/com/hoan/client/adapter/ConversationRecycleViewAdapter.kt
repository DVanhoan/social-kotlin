package com.hoan.client.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hoan.client.R
import com.hoan.client.network.response.ConversationItem
import com.squareup.picasso.Picasso

class ConversationRecyclerViewAdapter(
    private var conversations: List<ConversationItem>,
    private val conversationClickListener: ConversationClickListener
) : RecyclerView.Adapter<ConversationRecyclerViewAdapter.ConversationViewHolder>() {

    inner class ConversationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProfilePicture: ImageView = itemView.findViewById(R.id.iv_profile_picture)
        val tvConversationName: TextView = itemView.findViewById(R.id.tv_conversation_name)
        val tvLastMessage: TextView = itemView.findViewById(R.id.tv_last_message)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_conversation, parent, false)
        return ConversationViewHolder(view)
    }

    override fun getItemCount(): Int = conversations.size

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        val conversation = conversations[position]

        holder.tvConversationName.text = conversation.other_participant?.username ?: conversation.last_message
        holder.tvLastMessage.text = conversation.last_message ?: ""

        val profileUrl = conversation.other_participant?.profile_picture
        if (!profileUrl.isNullOrEmpty()) {
            Picasso.get()
                .load(profileUrl)
                .placeholder(R.drawable.icon)
                .into(holder.ivProfilePicture)
        } else {
            holder.ivProfilePicture.setImageResource(R.drawable.icon)
        }


        holder.itemView.setOnClickListener {
            conversationClickListener.onClickConversation(conversation)
        }
    }

    fun updateConversations(newConversations: List<ConversationItem>) {
        this.conversations = newConversations
        notifyDataSetChanged()
    }
}

interface ConversationClickListener {
    fun onClickConversation(conversation: ConversationItem)
}
