package com.hoan.client.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hoan.client.R
import com.hoan.client.databinding.ItemPostBinding
import com.hoan.client.fragment.CommentsFragment
import com.hoan.client.network.response.PostResponse
import com.squareup.picasso.Picasso

class PostsRecyclerViewAdapter(
    private val currentUserId: Long,
    private val reactionListener: ReactionListener,
    private val settingsListener: SettingsListener
) : ListAdapter<PostResponse, PostsRecyclerViewAdapter.VH>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PostResponse>() {
            override fun areItemsTheSame(a: PostResponse, b: PostResponse) = a.id == b.id
            override fun areContentsTheSame(a: PostResponse, b: PostResponse) = a == b
        }
    }

    inner class VH(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: PostResponse) {
            binding.userDetailInclude.tvUsername.text = post.user?.username.orEmpty()
            Picasso.get()
                .load(post.user?.profilePicture)
                .placeholder(R.drawable.profile_placeholder)
                .into(binding.userDetailInclude.civProfilePicture)

            Picasso.get()
                .load(post.mainPhoto)
                .placeholder(R.color.primaryAccent)
                .into(binding.mainPhoto.imageHolder)

            if (post.content.isNullOrEmpty()) {
                binding.content.visibility = View.GONE
            } else {
                binding.content.visibility = View.VISIBLE
                binding.content.text = post.content
            }

            binding.userDetailInclude.tvPostTime.text = post.postingTime


            binding.commentIcon.setOnClickListener {
                val activity = binding.root.context as? androidx.fragment.app.FragmentActivity
                activity?.supportFragmentManager?.let { fm ->
                    fm.beginTransaction()
                        .add(R.id.fragment_container_view,
                            CommentsFragment.newInstance(post),
                            "COMMENTS_FRAGMENT")
                        .addToBackStack("COMMENTS_FRAGMENT")
                        .commit()
                }
            }
            binding.commentCount.text = post.commentCount.toString()


            val iconRes = when (post.userReaction) {
                "like"  -> R.drawable.ic_like
                "love"  -> R.drawable.ic_love
                "haha"  -> R.drawable.ic_haha
                "sad"   -> R.drawable.ic_sad
                "tired" -> R.drawable.ic_tired
                else    -> R.drawable.ic_face_grin_solid
            }
            binding.reactionIcon.setImageResource(iconRes)
            binding.reactionIcon.setOnClickListener {
                reactionListener.reaction(post.id)
            }
            binding.reactionCount.text = post.reactionCount.toString()


            binding.userDetailInclude.ibSettings.setOnClickListener { view ->
                val popup = PopupMenu(view.context, view)
                if (post.user?.id == currentUserId) {
                    popup.menuInflater.inflate(R.menu.menu_post_creator, popup.menu)
                } else {
                    popup.menuInflater.inflate(R.menu.menu_post_viewer, popup.menu)
                }
                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.menu_edit_post   -> settingsListener.onEditPost(post)
                        R.id.menu_delete_post -> settingsListener.onDeletePost(post)
                        R.id.menu_report_post -> settingsListener.onReportPost(post)
                    }
                    true
                }
                popup.show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    interface ReactionListener {
        fun reaction(postId: Long)
    }
    interface SettingsListener {
        fun onEditPost(post: PostResponse)
        fun onDeletePost(post: PostResponse)
        fun onReportPost(post: PostResponse)
    }
}
