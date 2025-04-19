package com.hoan.client.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hoan.client.R
import com.hoan.client.adapter.CommentsRecyclerViewAdapter
import com.hoan.client.databinding.FragmentCommentsBinding
import com.hoan.client.network.response.CommentResponse
import com.hoan.client.network.response.PostResponse
import com.hoan.client.network.response.ReactionResponse
import com.hoan.client.network.RetrofitInstance
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommentsFragment(
    private val post: PostResponse,
    private val comments: List<CommentResponse>
) : Fragment(R.layout.fragment_comments) {

    private var _binding: FragmentCommentsBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    private val sharedPrefName = "user_shared_preference"
    private val picasso: Picasso by lazy { Picasso.get() }

    private lateinit var commentsRecyclerViewAdapter: CommentsRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommentsBinding.inflate(inflater, container, false)

        sharedPreferences = requireActivity().getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt", "") ?: ""
        RetrofitInstance.setToken(token)

        binding.backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
            requireActivity().findViewById<View>(R.id.toolbar).visibility = View.VISIBLE
        }

        binding.sendCommentButton.setOnClickListener {
            val commentText = binding.etAddComment.text.toString()
            sendComment(post.id, commentText)
            binding.etAddComment.setText("")
            binding.etAddComment.clearFocus()
        }

        setupRecyclerView()
        getCommentsOnPost(post.id)

        return binding.root
    }

    private fun setupRecyclerView() {
        val llm = LinearLayoutManager(context).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
        commentsRecyclerViewAdapter = CommentsRecyclerViewAdapter()
        commentsRecyclerViewAdapter.reloadComments(comments)
        val list = binding.root.findViewById<RecyclerView>(R.id.comments_recycler_view)
        list.layoutManager = llm
        list.adapter = commentsRecyclerViewAdapter
    }

    private fun getCommentsOnPost(postId: Long) {
        RetrofitInstance.commentService.getCommentsOnPost(postId)
            .enqueue(object : Callback<List<CommentResponse>> {
                override fun onResponse(
                    call: Call<List<CommentResponse>>,
                    response: Response<List<CommentResponse>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        getCommentsOnPostSuccess(response.code(), response.body()!!)
                    } else {
                        generalError(response.code(), Exception("Error getting comments: ${response.message()}"))
                    }
                }
                override fun onFailure(call: Call<List<CommentResponse>>, t: Throwable) {
                    generalError(500, t)
                }
            })
    }

    private fun getCommentsOnPostSuccess(statusCode: Int, responseBody: List<CommentResponse>) {
        Log.d("GET_COMMENTS_ON_POST", "Successfully got comments: $responseBody Status code: $statusCode")
        if (responseBody.isNotEmpty()) {
            commentsRecyclerViewAdapter.reloadComments(responseBody)
        }
    }

    private fun sendComment(postId: Long, comment: String) {
        RetrofitInstance.commentService.comment(comment, postId)
            .enqueue(object : Callback<CommentResponse> {
                override fun onResponse(call: Call<CommentResponse>, response: Response<CommentResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        onCommentSuccess(response.code(), response.body()!!)
                    } else {
                        generalError(response.code(), Exception("Error posting comment: ${response.message()}"))
                    }
                }
                override fun onFailure(call: Call<CommentResponse>, t: Throwable) {
                    generalError(500, t)
                }
            })
    }

    private fun onCommentSuccess(statusCode: Int, responseBody: CommentResponse) {
        Log.d("COMMENT_SUCCESSFUL", "Status code: $statusCode")
        commentsRecyclerViewAdapter.addComment(responseBody)
    }

    private fun generalError(statusCode: Int, e: Throwable) {
        Log.e("API_CALL_ERROR", "Error $statusCode during API call")
        e.printStackTrace()
    }

    companion object {
        @JvmStatic
        fun newInstance(
            post: PostResponse,
            comments: List<CommentResponse>,
        ) = CommentsFragment(post, comments)
    }
}
