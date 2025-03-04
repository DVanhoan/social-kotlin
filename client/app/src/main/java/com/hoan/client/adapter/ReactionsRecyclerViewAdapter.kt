package com.hoan.client.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hoan.client.database.repository.CacheService
import com.hoan.client.databinding.ReactionItemBinding
import com.hoan.client.network.response.ReactionResponse

class ReactionsRecyclerViewAdapter : RecyclerView.Adapter<ReactionsRecyclerViewAdapter.ReactionItemViewHolder>() {

    private val reactionList = mutableListOf<ReactionResponse>()

    inner class ReactionItemViewHolder(private val binding: ReactionItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val reaction = reactionList[position]
            binding.reactionUsername.text = reaction.username
            binding.lateTime.text = reaction.reactionTime
            CacheService.cacheReactionImage(reaction.imageName, binding.reactionProfilePicture)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReactionItemViewHolder {
        val binding = ReactionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
