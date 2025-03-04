package com.hoan.client.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hoan.client.database.repository.CacheService
import com.hoan.client.databinding.CommentItemBinding
import com.hoan.client.network.response.CommentResponse

class CommentsRecyclerViewAdapter : RecyclerView.Adapter<CommentsRecyclerViewAdapter.CommentItemViewHolder>() {

    private val commentList = mutableListOf<CommentResponse>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentItemViewHolder {
        val binding = CommentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentItemViewHolder, position: Int) {
        holder.bind(position)
        holder.itemView.setOnClickListener {
            Log.d("POSITION", position.toString())
        }
    }

    override fun getItemCount(): Int = commentList.size

    inner class CommentItemViewHolder(private val binding: CommentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val comment = commentList[position]
            binding.commenterName.text = comment.username
            binding.commentText.text = comment.text
            binding.lateTime.text = comment.commentTime
            CacheService.cacheProfilePicture(comment.userId, binding.commenterProfilePicture)
        }
    }

    fun addComment(comment: CommentResponse) {
        val size = commentList.size
        commentList.add(comment)
        notifyItemInserted(size)
    }

    fun reloadComments(comments: List<CommentResponse>) {
        commentList.clear()
        commentList.addAll(comments)
        notifyDataSetChanged()
    }
}
