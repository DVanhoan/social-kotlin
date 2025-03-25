package com.hoan.client.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hoan.client.databinding.ItemConversationBinding
import com.hoan.client.network.response.ConversationItem

class MessagesRecycleViewAdapter(
    private val context: Context,
    private val list: List<ConversationItem>,
    private val messageClickInterface: MessageOnClickInterface
) : RecyclerView.Adapter<MessagesRecycleViewAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemConversationBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemConversationBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = list[position]
        holder.binding.tvConversationName.text = currentItem.name
        holder.binding.tvLastMessage.text = currentItem.last_message

        Glide
            .with(context)
            .load(currentItem.other_participant?.profile_picture)
            .into(holder.binding.ivProfilePicture)

        holder.itemView.setOnClickListener {
            messageClickInterface.onClickMessage(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

interface MessageOnClickInterface {
    fun onClickMessage(item: Message)
}