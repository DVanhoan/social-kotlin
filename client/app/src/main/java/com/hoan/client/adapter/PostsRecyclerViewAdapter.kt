package com.hoan.client.adapter

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.hoan.client.R
import com.hoan.client.databinding.ItemPostBinding
import com.hoan.client.fragment.CommentsFragment
import com.hoan.client.network.response.CommentResponse
import com.hoan.client.network.response.PostResponse
import com.hoan.client.network.response.UserResponse
import com.squareup.picasso.Picasso


class PostsRecyclerViewAdapter(
    private var currentUserId: Long,
    private val reactionListener: ReactionListener,
    private val settingsListener: SettingsListener,
    private val activity: FragmentActivity
) : RecyclerView.Adapter<PostsRecyclerViewAdapter.PostItemViewHolder>() {

    private val postList = mutableListOf<PostResponse>()
    private val commentsOnPosts = hashMapOf<Long, List<CommentResponse>>()
    private val picasso: Picasso by lazy { Picasso.get() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostItemViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostItemViewHolder(binding)
    }

    override fun getItemCount(): Int = postList.size

    override fun onBindViewHolder(holder: PostItemViewHolder, position: Int) {
        holder.bind(postList[position])
    }

    inner class PostItemViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: PostResponse) {
            if (post.deleted) {
                itemView.visibility = View.GONE
                return
            }

            val user: UserResponse? = post.user


            binding.userDetailInclude.tvUsername.text = user?.username ?: "Unknown"

            picasso.load(user?.profilePicture ?: "https://static2.yan.vn/YanNews/2167221/202102/facebook-cap-nhat-avatar-doi-voi-tai-khoan-khong-su-dung-anh-dai-dien-e4abd14d.jpg")
                .placeholder(R.color.primaryAccent)
                .into(binding.userDetailInclude.civProfilePicture)


            picasso.load(post.mainPhoto)
                .placeholder(R.color.primaryAccent)
                .into(binding.mainPhoto.imageHolder)

            val content = post.content ?: ""
            if (content.isEmpty()) {
                binding.content.visibility = View.GONE
            } else {
                binding.content.visibility = View.VISIBLE
                binding.content.text = content
            }

            val comments = commentsOnPosts[post.id] ?: emptyList()

            binding.commentIcon.setOnClickListener {
                showFullscreenCommentsFragment(post, comments)
            }

            binding.userDetailInclude.tvPostTime.text = post.postingTime


            binding.reactionIcon.setOnClickListener {
                reactionListener.reaction(post.id, adapterPosition)
            }


            binding.userDetailInclude.ibSettings.setOnClickListener { view ->
                showSettingsMenu(view, post)
            }

        }
    }

    fun setPosts(posts: List<PostResponse>) {
        postList.clear()
        postList.addAll(posts.sortedByDescending { it.id })
        notifyDataSetChanged()
    }


    private fun showSettingsMenu(anchorView: View, post: PostResponse) {
        val popup = PopupMenu(anchorView.context, anchorView)
        if (post.user?.id == currentUserId) {
            popup.menuInflater.inflate(R.menu.menu_post_creator, popup.menu)
        } else {
            popup.menuInflater.inflate(R.menu.menu_post_viewer, popup.menu)
        }
        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.menu_edit_post -> {
                    settingsListener.onEditPost(post)
                    true
                }
                R.id.menu_delete_post -> {
                    settingsListener.onDeletePost(post)
                    true
                }
                R.id.menu_report_post -> {
                    settingsListener.onReportPost(post)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }


    private fun showFullscreenCommentsFragment(
        post: PostResponse,
        comments: List<CommentResponse>,
    ) {
        activity.findViewById<View>(R.id.toolbar)?.visibility = View.GONE
        val fragment = CommentsFragment.newInstance(post, comments)

        (activity as? FragmentActivity)?.supportFragmentManager?.let { fm ->
            val transaction = fm.beginTransaction()
            val feedFragment = fm.findFragmentByTag("LIST_POST_FRAGMENT")
            if (feedFragment != null) {
                transaction.hide(feedFragment)
            }
            transaction.add(R.id.fragment_container_view, fragment, "COMMENTS_FRAGMENT")
            transaction.addToBackStack("COMMENTS_FRAGMENT")
            transaction.commit()
        }
    }



    fun addComments(postId: Long, comments: List<CommentResponse>) {
        commentsOnPosts[postId] = comments
        val index = postList.indexOfFirst { it.id == postId }
        if (index >= 0) notifyItemChanged(index)
    }

    interface ReactionListener {
        fun reaction(postId: Long, position: Int)
    }

    interface SettingsListener {
        fun onEditPost(post: PostResponse)
        fun onDeletePost(post: PostResponse)
        fun onReportPost(post: PostResponse)
    }
}
