package com.hoan.client.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hoan.client.MessageActivity
import com.hoan.client.R
import com.hoan.client.databinding.ItemConversationBinding
import com.hoan.client.network.response.ConversationItem

class ConversationRecyclerViewAdapter(
    private var conversations: List<ConversationItem>
) : RecyclerView.Adapter<ConversationRecyclerViewAdapter.ConversationViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val binding = ItemConversationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ConversationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        holder.bind(position)
        holder.itemView.setOnClickListener {
            Log.d("POSITION", position.toString())

            val conversation = conversations[position]

            val intent = Intent(holder.itemView.context, MessageActivity::class.java)

            intent.putExtra("conversationId", conversation.id.toLong())

            holder.itemView.context.startActivity(intent)
        }
    }


    inner class ConversationViewHolder(private val binding: ItemConversationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val conversation = conversations[position]
            binding.tvConversationName.text = conversation.name
            binding.tvLastMessage.text = conversation.last_message
            binding.tvTimestamp.text = conversation.last_message_time

            Glide.with(binding.ivProfilePicture)
                .load(conversation.other_participant?.profile_picture)
                .placeholder(R.drawable.profile_placeholder)
                .error(R.drawable.profile_placeholder)
                .circleCrop()
                .into(binding.ivProfilePicture)

        }
    }

    override fun getItemCount(): Int = conversations.size



    fun updateConversations(newConversations: List<ConversationItem>) {
        this.conversations = newConversations
        notifyDataSetChanged()
    }
}
