package com.hoan.client.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hoan.client.databinding.ItemReactionBinding
import com.hoan.client.network.response.ReactionResponse
import com.squareup.picasso.Picasso

class ReactionsRecyclerViewAdapter : RecyclerView.Adapter<ReactionsRecyclerViewAdapter.ReactionItemViewHolder>() {

    private val reactionList = mutableListOf<ReactionResponse>()
    private val picasso: Picasso by lazy { Picasso.get() }

    inner class ReactionItemViewHolder(private val binding: ItemReactionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val reaction = reactionList[position]
            binding.reactionUsername.text = reaction.user?.username
            binding.lateTime.text = reaction.reactionTime
            picasso.load(reaction.user?.profilePicture).into(binding.reactionProfilePicture)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReactionItemViewHolder {
        val binding = ItemReactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReactionItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReactionItemViewHolder, position: Int) {
        holder.bind(position)
        holder.itemView.setOnClickListener {
            Log.d("POSITION", position.toString())
        }
    }

    override fun getItemCount(): Int = reactionList.size

    fun addReaction(reaction: ReactionResponse) {
        val size = reactionList.size
        reactionList.add(reaction)
        notifyItemInserted(size)
    }

    fun reloadReactions(reactions: List<ReactionResponse>) {
        reactionList.clear()
        reactionList.addAll(reactions)
        notifyDataSetChanged()
    }
}
