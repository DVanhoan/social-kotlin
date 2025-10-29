package com.hoan.client.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hoan.client.network.RetrofitInstance
import com.hoan.client.network.request.ReactRequest
import com.hoan.client.network.response.PostResponse
import com.hoan.client.network.response.ReactionResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostsViewModel : ViewModel() {
    // LiveData expose ra UI
    private val _posts = MutableLiveData<List<PostResponse>>()
    val posts: LiveData<List<PostResponse>> = _posts

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Giữ list cục bộ để dễ update
    private val currentPosts = mutableListOf<PostResponse>()

    fun loadPosts() {
        RetrofitInstance.postService.getAllPosts()
            .enqueue(object : Callback<List<PostResponse>> {
                override fun onResponse(
                    call: Call<List<PostResponse>>,
                    response: Response<List<PostResponse>>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body().orEmpty()
                        currentPosts.apply {
                            clear()
                            addAll(body)
                        }
                        _posts.value = currentPosts.toList()
                        _error.value = null
                    } else {
                        _error.value = "Error loading posts: ${response.message()}"
                    }
                }
                override fun onFailure(call: Call<List<PostResponse>>, t: Throwable) {
                    _error.value = "Network error: ${t.localizedMessage}"
                }
            })
    }

    fun react(postId: Long, reactionType: String) {
        val req = ReactRequest(
            post_id = postId,
            reaction = reactionType
        )
        RetrofitInstance.reactionService.react(req)
            .enqueue(object : Callback<ReactionResponse> {
                override fun onResponse(
                    call: Call<ReactionResponse>,
                    response: Response<ReactionResponse>
                ) {
                    if (response.isSuccessful) {
                        val idx = currentPosts.indexOfFirst { it.id == postId }
                        if (idx != -1) {
                            val old = currentPosts[idx]
                            val updated = old.copy(
                                userReaction  = reactionType,
                                reactionCount = response.body()?.reaction_count ?: old.reactionCount,
                            )
                            currentPosts[idx] = updated
                            _posts.value = currentPosts.toList()
                        }
                    } else {
                        _error.value = "React failed: ${response.message()}"
                    }
                }

                override fun onFailure(call: Call<ReactionResponse>, t: Throwable) {
                    _error.value = "Network error: ${t.localizedMessage}"
                }
            })
    }
}
