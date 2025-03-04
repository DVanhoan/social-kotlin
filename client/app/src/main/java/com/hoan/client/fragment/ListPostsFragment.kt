package com.hoan.client.fragment

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.dhaval2404.imagepicker.ImagePicker
import com.hoan.client.R
import com.hoan.client.adapter.PostsRecyclerViewAdapter
import com.hoan.client.constant.Constants
import com.hoan.client.databinding.FragmentListPostsBinding
import com.hoan.client.network.response.CommentResponse
import com.hoan.client.network.response.PostResponse
import com.hoan.client.network.response.ReactionResponse
import com.hoan.client.network.response.UserResponse
import com.hoan.client.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

class ListPostsFragment(private var user: UserResponse) :
    Fragment(R.layout.fragment_list_posts),
    EditProfileFragment.EditedUserListener,
    PostsRecyclerViewAdapter.ReactionListener {

    private var _binding: FragmentListPostsBinding? = null
    private val binding get() = _binding!!

    private lateinit var imagePicker: ImagePicker.Builder
    private lateinit var sharedPreferences: SharedPreferences

    private var reactionOnPostID: Long? = null
    private var reactionPosition: Int? = null

    private lateinit var postsRecyclerViewAdapter: PostsRecyclerViewAdapter

    private val sharedPrefName = "user_shared_preference"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListPostsBinding.inflate(inflater, container, false)
        sharedPreferences = requireActivity().getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt", "") ?: ""
        RetrofitInstance.setToken(token)

        imagePicker = ImagePicker.with(requireActivity())
            .cropSquare()
            .compress(1024)
            .maxResultSize(720, 1080)
            .cameraOnly()
            .galleryMimeTypes(arrayOf("image/png", "image/jpg", "image/jpeg"))

        getTodaysPostByUser(user.id)
        setupRecyclerView()

        return binding.root
    }

    override fun reaction(postId: Long, position: Int) {
        Log.d("REACTION", "Reacting to post: $postId")
        reactionOnPostID = postId
        reactionPosition = position
        imagePicker.createIntent { intent ->
            startForReactionImageResult.launch(intent)
        }
    }

    private fun react(multipart: MultipartBody.Part) {
        RetrofitInstance.reactionService.react(
            reaction = multipart,
            postId = reactionOnPostID!!
        ).enqueue(object : Callback<ReactionResponse> {
            override fun onResponse(
                call: Call<ReactionResponse>,
                response: Response<ReactionResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    onReactionSuccess(response.code(), response.body()!!, reactionPosition!!)
                } else {
                    generalError(response.code(), Exception("Error reacting: ${response.message()}"))
                }
            }

            override fun onFailure(call: Call<ReactionResponse>, t: Throwable) {
                generalError(500, t)
            }
        })
    }

    private fun onReactionSuccess(statusCode: Int, responseBody: ReactionResponse, position: Int) {
        Log.d("REACTION_SUCCESSFUL", "Status code: $statusCode")
        postsRecyclerViewAdapter.notifyItemChanged(position + 1)
    }

    private val startForReactionImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    val fileUri: Uri = data?.data!!
                    val multipart = getMultiPartReactionImageFromUri(fileUri)
                    if (reactionOnPostID != null && reactionPosition != null) {
                        react(multipart)
                    }
                    reactionOnPostID = null
                    reactionPosition = null
                }
                ImagePicker.RESULT_ERROR -> {
                    Constants.showErrorSnackbar(
                        requireContext(),
                        layoutInflater,
                        ImagePicker.getError(data)
                    )
                }
                else -> {
                    Constants.showErrorSnackbar(
                        requireContext(),
                        layoutInflater,
                        "Upload Cancelled"
                    )
                }
            }
        }

    private fun getMultiPartReactionImageFromUri(uri: Uri): MultipartBody.Part {
        val file = File(uri.path!!)
        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("reaction", file.name, requestBody)
    }

    private fun getTodaysPostByUser(userId: Long) {
        RetrofitInstance.postService.getTodaysPostByUser(userId)
            .enqueue(object : Callback<PostResponse> {
                override fun onResponse(call: Call<PostResponse>, response: Response<PostResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        getTodaysPostByUserSuccess(response.code(), response.body()!!)
                    } else {
                        generalError(response.code(), Exception("Error fetching today's post: ${response.message()}"))
                    }
                }

                override fun onFailure(call: Call<PostResponse>, t: Throwable) {
                    generalError(500, t)
                }
            })
    }

    private fun getTodaysPostByUserSuccess(statusCode: Int, responseBody: PostResponse) {
        Log.d("GET_TODAYS_POST", "Successfully got today's post: $responseBody Status code: $statusCode")
        postsRecyclerViewAdapter.setUserCard(user)
        postsRecyclerViewAdapter.setUserPost(responseBody)
        getPostsFromFriends()
        getCommentsOnPost(responseBody.id)
        getReactionsOnPost(responseBody.id)
    }

    private fun getPostsFromFriends() {
        RetrofitInstance.postService.getPostsFromFriends()
            .enqueue(object : Callback<List<PostResponse>> {
                override fun onResponse(call: Call<List<PostResponse>>, response: Response<List<PostResponse>>) {
                    if (response.isSuccessful && response.body() != null) {
                        getPostsFromFriendsSuccess(response.code(), response.body()!!)
                    } else {
                        generalError(response.code(), Exception("Error fetching posts from friends: ${response.message()}"))
                    }
                }
                override fun onFailure(call: Call<List<PostResponse>>, t: Throwable) {
                    generalError(500, t)
                }
            })
    }

    private fun getPostsFromFriendsSuccess(statusCode: Int, responseBody: List<PostResponse>) {
        Log.d("GET_POSTS_FROM_FRIENDS", "Successfully got posts from friends: $responseBody Status code: $statusCode")
        postsRecyclerViewAdapter.addAll(responseBody)
        responseBody.forEach {
            getCommentsOnPost(it.id)
            getReactionsOnPost(it.id)
        }
    }

    private fun getReactionsOnPost(postId: Long) {
        RetrofitInstance.reactionService.getReactionsOnPost(postId)
            .enqueue(object : Callback<List<ReactionResponse>> {
                override fun onResponse(call: Call<List<ReactionResponse>>, response: Response<List<ReactionResponse>>) {
                    if (response.isSuccessful && response.body() != null) {
                        getReactionsOnPostSuccess(response.code(), response.body()!!)
                    } else {
                        generalError(response.code(), Exception("Error fetching reactions: ${response.message()}"))
                    }
                }
                override fun onFailure(call: Call<List<ReactionResponse>>, t: Throwable) {
                    generalError(500, t)
                }
            })
    }

    private fun getReactionsOnPostSuccess(statusCode: Int, responseBody: List<ReactionResponse>) {
        Log.d("GET_REACTIONS", "Successfully got reactions: $responseBody Status code: $statusCode")
        if (responseBody.isEmpty()) return
        postsRecyclerViewAdapter.addReactions(responseBody[0].postId, responseBody)
    }

    private fun getCommentsOnPost(postId: Long) {
        RetrofitInstance.commentService.getCommentsOnPost(postId)
            .enqueue(object : Callback<List<CommentResponse>> {
                override fun onResponse(call: Call<List<CommentResponse>>, response: Response<List<CommentResponse>>) {
                    if (response.isSuccessful && response.body() != null) {
                        getCommentsOnPostSuccess(response.code(), response.body()!!)
                    } else {
                        generalError(response.code(), Exception("Error fetching comments: ${response.message()}"))
                    }
                }
                override fun onFailure(call: Call<List<CommentResponse>>, t: Throwable) {
                    generalError(500, t)
                }
            })
    }

    private fun getCommentsOnPostSuccess(statusCode: Int, responseBody: List<CommentResponse>) {
        Log.d("GET_COMMENTS", "Successfully got comments: $responseBody Status code: $statusCode")
        if (responseBody.isEmpty()) return
        postsRecyclerViewAdapter.addComments(responseBody[0].postId, responseBody)
    }

    private fun generalError(statusCode: Int, e: Throwable) {
        Log.e("API_CALL_ERROR", "Error $statusCode during API call")
        e.printStackTrace()
    }

    private fun setupRecyclerView() {
        val llm = LinearLayoutManager(context).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
        postsRecyclerViewAdapter = PostsRecyclerViewAdapter(this, requireActivity())
        val list = binding.root.findViewById<RecyclerView>(R.id.posts_recycler_view)
        list.layoutManager = llm
        list.adapter = postsRecyclerViewAdapter
    }

    override fun updateUserDetails(user: UserResponse) {
        Log.d("UPDATE_USER", "Updating user details: $user")
        this.user = user
        postsRecyclerViewAdapter.updateUser(user)
    }

    // Callback cho nút Back của hệ thống
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (requireActivity().supportFragmentManager.fragments.size == 1)
                        requireActivity().finish()
                    else
                        requireActivity().supportFragmentManager.popBackStack()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(user: UserResponse) = ListPostsFragment(user)
    }
}
