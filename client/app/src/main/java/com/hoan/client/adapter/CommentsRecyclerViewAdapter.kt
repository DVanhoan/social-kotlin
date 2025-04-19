package com.hoan.client.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hoan.client.databinding.ItemCommentBinding
import com.hoan.client.network.response.CommentResponse

class CommentsRecyclerViewAdapter : RecyclerView.Adapter<CommentsRecyclerViewAdapter.CommentItemViewHolder>() {

    private val commentList = mutableListOf<CommentResponse>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentItemViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentItemViewHolder, position: Int) {
        holder.bind(position)
        holder.itemView.setOnClickListener {
            Log.d("POSITION", position.toString())
        }
    }

    override fun getItemCount(): Int = commentList.size

    inner class CommentItemViewHolder(private val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val comment = commentList[position]
            binding.commenterName.text = comment.user?.username
            binding.commentText.text = comment.text
            binding.lateTime.text = comment.commentTime

            Glide.with(binding.commenterProfilePicture)
                .load(comment.user?.profilePicture)
                .placeholder(android.R.color.holo_blue_light)
                .into(binding.commenterProfilePicture)
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
