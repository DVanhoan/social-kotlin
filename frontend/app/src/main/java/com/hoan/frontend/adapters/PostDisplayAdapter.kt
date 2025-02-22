package com.hoan.frontend.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hoan.frontend.databinding.PostfeedmainItemBinding
import com.hoan.frontend.models.entities.Post

class PostDisplayAdapter (
    private val context:Context,
    private val list: List<Post>

) : RecyclerView.Adapter<PostDisplayAdapter.ViewHolder>() {

    private val likedProducts = mutableSetOf<String>()


    inner class ViewHolder(val binding: PostfeedmainItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(PostfeedmainItemBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = list[position]
        holder.binding.tvPostContent.text = currentItem.description
        holder.binding.tvPostTime.text = currentItem.created_at

        Glide
            .with(context)
            .load(currentItem.image)
            .into(holder.binding.ivPostImage)

    }


    override fun getItemCount(): Int {
        return list.size
    }
}

interface ProductOnClickInterface {
    fun onClickPost(item: Post)
}

interface LikeOnClickInterface{
    fun onClickLike(item : Post)
    fun onUnlike(item: Post)
}