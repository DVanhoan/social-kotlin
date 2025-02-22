package com.hoan.frontend.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hoan.frontend.databinding.ItemPostBinding
import com.hoan.frontend.models.entities.Post

class PostDisplayAdapter(
    private val context: Context,
    private val posts: List<Post>,
    private val onPostClick: ((Post) -> Unit)? = null,
    private val onLikeClick: ((Post) -> Unit)? = null,
    private val onCommentClick: ((Post) -> Unit)? = null
) : RecyclerView.Adapter<PostDisplayAdapter.PostViewHolder>() {

    inner class PostViewHolder(val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        val b = holder.binding

        // Avatar
        Glide.with(context)
            .load(post.user?.pic)
            .placeholder(com.hoan.frontend.R.drawable.shoe10)
            .into(b.ivProfile)

        // Username + created_at
        b.tvUsernameAndTime.text = "@${post.user?.username} • ${post.created_at}"

        // Nội dung
        b.tvPostContent.text = post.description

        // Ảnh bài viết (nếu có)
        if (!post.image.isNullOrEmpty()) {
            b.ivPostImage.visibility = View.VISIBLE
            Glide.with(context).load(post.image).into(b.ivPostImage)
        } else {
            b.ivPostImage.visibility = View.GONE
        }

        // Like count
        b.tvLikeCount.text = post.likes_count.toString()
        // Comment count
        b.tvCommentCount.text = post.comments_count.toString()

        // Nếu muốn đổi icon khi đã like (tuỳ API)
        // val likeIcon = if (post.is_liked) R.drawable.ic_like_filled else R.drawable.ic_like
        // b.ivLike.setImageResource(likeIcon)

        // Xử lý sự kiện click
        b.root.setOnClickListener {
            onPostClick?.invoke(post)
        }
        b.ivLike.setOnClickListener {
            onLikeClick?.invoke(post)
        }
        b.ivComment.setOnClickListener {
            onCommentClick?.invoke(post)
        }
    }

    override fun getItemCount(): Int = posts.size
}