package com.hoan.client.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.hoan.client.R
import com.hoan.client.constant.Constants
import com.hoan.client.database.repository.CacheService
import com.hoan.client.databinding.PostItemBinding
import com.hoan.client.databinding.UserCardBinding
import com.hoan.client.fragment.CommentsFragment
import com.hoan.client.network.response.CommentResponse
import com.hoan.client.network.response.PostResponse
import com.hoan.client.network.response.ReactionResponse
import com.hoan.client.network.response.UserResponse
import com.hoan.client.network.RetrofitInstance
import java.sql.Timestamp

class PostsRecyclerViewAdapter(
    private val reactionListener: ReactionListener,
    private val activity: FragmentActivity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var userCardDetails: UserResponse? = null
    private var userPost: PostResponse? = null
    private val postList = mutableListOf<PostResponse>()
    private var commentsOnPosts: HashMap<Long, List<CommentResponse>> = HashMap()
    private var reactionsOnPosts: HashMap<Long, List<ReactionResponse>> = HashMap()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == USER_CARD_VIEW) {
            UserViewHolder(
                UserCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        } else {
            PostItemViewHolder(
                PostItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position == 0) {
            (holder as UserViewHolder).bind(position)
        } else {
            (holder as PostItemViewHolder).bind(position)
        }
        holder.itemView.setOnClickListener { Log.d("POSITION", position.toString()) }
    }

    override fun getItemCount(): Int = postList.size + if (userPost == null) 0 else 1

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) USER_CARD_VIEW else POST_VIEW
    }

    private inner class UserViewHolder(private val binding: UserCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var inputMethodManager: InputMethodManager? = null

        fun bind(position: Int) {
            val postId = userPost?.id
            val comments = if (postId != null) commentsOnPosts[postId] ?: emptyList() else emptyList()
            val reactions = if (postId != null) reactionsOnPosts[postId] ?: emptyList() else emptyList()

            if (userPost == null || userCardDetails == null) return

            binding.userDetailInclude.tvUsername.text = userCardDetails?.username
            CacheService.cacheProfilePicture(userCardDetails!!, binding.userDetailInclude.civProfilePicture)
            CacheService.cachePostImage(userPost!!.mainPhoto, binding.mainPhoto.imageHolder)
            CacheService.cachePostImage(userPost!!.selfiePhoto, binding.selfiePhoto.imageHolder)

            binding.comments.text =
                if (comments.isNotEmpty()) {
                    "See ${comments.size} comment" + if (comments.size > 1) "s" else ""
                } else {
                    "Add a comment..."
                }

            binding.comments.setOnClickListener {
                showFullscreenCommentsFragment(userPost!!, comments, reactions)
            }

            binding.reaction1.visibility = View.GONE
            binding.reaction2.visibility = View.GONE
            binding.reaction3.visibility = View.GONE

            if (reactions.isNotEmpty()) {
                CacheService.cacheReactionImage(reactions[0].imageName, binding.reaction1)
                binding.reaction1.visibility = View.VISIBLE
            }
            if (reactions.size > 1) {
                CacheService.cacheReactionImage(reactions[1].imageName, binding.reaction2)
                binding.reaction2.visibility = View.VISIBLE
            }
            if (reactions.size > 2) {
                CacheService.cacheReactionImage(reactions[2].imageName, binding.reaction3)
                binding.reaction3.visibility = View.VISIBLE
            }

            binding.userDetailInclude.tvPostTime.text = userPost!!.postingTime

            binding.etDescription.setText(userPost!!.description ?: "")
            binding.etDescription.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val newDescription = binding.etDescription.text.toString()
                    editDescription(userPost!!.id, newDescription)
                    if (inputMethodManager == null) {
                        inputMethodManager =
                            binding.root.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    }
                    inputMethodManager!!.hideSoftInputFromWindow(binding.etDescription.windowToken, 0)
                    binding.etDescription.isCursorVisible = false
                    Log.d("DESCRIPTION", "Updating description")
                    true
                } else {
                    false
                }
            }
        }
    }

    private inner class PostItemViewHolder(private val binding: PostItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        var isSwapped = false
        var comments: List<CommentResponse> = emptyList()
        var reactions: List<ReactionResponse> = emptyList()

        fun bind(position: Int) {
            val post = postList[position - 1]
            comments = commentsOnPosts[post.id] ?: emptyList()
            reactions = reactionsOnPosts[post.id] ?: emptyList()

            if (post.deleted) {
                itemView.visibility = View.GONE
                return
            }

            binding.userDetailInclude.tvUsername.text = post.username
            val description = post.description ?: ""
            if (description.isEmpty()) binding.description.visibility = View.GONE
            else binding.description.text = description

            CacheService.cacheProfilePicture(post.userId, binding.userDetailInclude.civProfilePicture)
            CacheService.cachePostImage(post.mainPhoto, binding.mainPhoto.imageHolder)
            CacheService.cachePostImage(post.selfiePhoto, binding.selfiePhoto.imageHolder)

            binding.selfiePhoto.imageHolder.setOnClickListener {
                if (isSwapped) {
                    CacheService.cachePostImage(post.mainPhoto, binding.mainPhoto.imageHolder)
                    CacheService.cachePostImage(post.selfiePhoto, binding.selfiePhoto.imageHolder)
                } else {
                    CacheService.cachePostImage(post.selfiePhoto, binding.mainPhoto.imageHolder)
                    CacheService.cachePostImage(post.mainPhoto, binding.selfiePhoto.imageHolder)
                }
                isSwapped = !isSwapped
            }

            binding.comments.text =
                if (comments.isNotEmpty()) {
                    "See ${comments.size} comment" + if (comments.size > 1) "s" else ""
                } else {
                    "Add a comment..."
                }

            binding.reaction1.visibility = View.GONE
            binding.reaction2.visibility = View.GONE
            binding.reaction3.visibility = View.GONE

            if (reactions.isNotEmpty()) {
                CacheService.cacheReactionImage(reactions[0].imageName, binding.reaction1)
                binding.reaction1.visibility = View.VISIBLE
            }
            if (reactions.size > 1) {
                CacheService.cacheReactionImage(reactions[1].imageName, binding.reaction2)
                binding.reaction2.visibility = View.VISIBLE
            }
            if (reactions.size > 2) {
                CacheService.cacheReactionImage(reactions[2].imageName, binding.reaction3)
                binding.reaction3.visibility = View.VISIBLE
            }

            binding.reactionIcon.setOnClickListener {
                reactionListener.reaction(post.id, position - 1)
            }

            binding.userDetailInclude.tvPostTime.text = post.postingTime

            binding.comments.setOnClickListener {
                showFullscreenCommentsFragment(post, comments, reactions)
            }
            binding.commentIcon.setOnClickListener {
                showFullscreenCommentsFragment(post, comments, reactions)
            }
        }
    }

    private fun showFullscreenCommentsFragment(
        post: PostResponse,
        comments: List<CommentResponse>,
        reactions: List<ReactionResponse>
    ) {
        activity.findViewById<View>(R.id.toolbar).visibility = View.GONE
        val fragment: Fragment = CommentsFragment.newInstance(post, comments, reactions)
        activity.supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, fragment, "COMMENTS_FRAGMENT")
            .addToBackStack(fragment.id.toString())
            .commit()
    }

    interface ReactionListener {
        fun reaction(postId: Long, position: Int)
    }

    companion object {
        private const val USER_CARD_VIEW = 1
        private const val POST_VIEW = 2
    }

    private fun editDescription(postId: Long, description: String) {
        RetrofitInstance.postService.addDescription(postId, description)
            .enqueue(object : retrofit2.Callback<PostResponse> {
                override fun onResponse(
                    call: retrofit2.Call<PostResponse>,
                    response: retrofit2.Response<PostResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        editDescriptionSuccess(response.code(), response.body()!!)
                    } else {
                        generalError(response.code(), Exception("Error updating description: ${response.message()}"))
                    }
                }

                override fun onFailure(call: retrofit2.Call<PostResponse>, t: Throwable) {
                    generalError(500, t)
                }
            })
    }

    private fun editDescriptionSuccess(statusCode: Int, responseBody: PostResponse) {
        Log.d("EDIT_DESCRIPTION", "Description updated. Status code: $statusCode")
    }

    private fun generalError(statusCode: Int, e: Throwable) {
        Log.e("API_CALL_ERROR", "Error $statusCode during API call")
        e.printStackTrace()
    }

    // Hàm tính thời gian trễ dựa vào beFakeTime và postingTime (được convert thành Timestamp)
    private fun calculateLateness(time1: String, time2: String): String {
        val beFakeTime: Timestamp = Constants.convertStringToTimestamp(time1)
        val postingTime: Timestamp = Constants.convertStringToTimestamp(time2)
        val diff: Long = postingTime.time - beFakeTime.time
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        var lateTimeText = when {
            days > 1 -> "$days days"
            days > 0 -> "$days day"
            hours > 1 -> "$hours hours"
            hours > 0 -> "$hours hour"
            minutes > 1 -> "$minutes minutes"
            minutes > 0 -> "$minutes minute"
            else -> "$seconds seconds"
        }
        lateTimeText += " late"
        return lateTimeText
    }

    fun setUserPost(post: PostResponse) {
        userPost = post
        notifyItemChanged(0)
    }

    fun setUserCard(userDetails: UserResponse) {
        this.userCardDetails = userDetails
        notifyItemInserted(0)
    }

    fun addComments(postId: Long, comments: List<CommentResponse>) {
        commentsOnPosts[postId] = comments
        if (userPost?.id == postId) {
            notifyItemChanged(0)
        } else {
            val post = postList.first { it.id == postId }
            notifyItemChanged(postList.indexOf(post) + 1)
        }
        Log.d("COMMENTS", "Comments added: $comments")
    }

    fun addReactions(postId: Long, reactions: List<ReactionResponse>) {
        reactionsOnPosts[postId] = reactions
        if (userPost?.id == postId) {
            notifyItemChanged(0)
        } else {
            val post = postList.first { it.id == postId }
            notifyItemChanged(postList.indexOf(post) + 1)
        }
        Log.d("REACTIONS", "Reactions added: $reactions")
    }

    fun addItem(post: PostResponse) {
        val size = postList.size
        postList.add(post)
        notifyItemInserted(size)
    }

    fun updateUser(user: UserResponse) {
        this.userCardDetails = user
        Log.d("UPDATE_USER", "Updating user on feed")
        notifyItemChanged(0)
    }

    fun addAll(posts: List<PostResponse>) {
        postList.addAll(posts.sortedByDescending { it.id })
        notifyItemRangeInserted(itemCount, posts.size)
    }
}
