package com.hoan.client.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hoan.client.network.RetrofitInstance
import com.hoan.client.network.response.CommentResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommentsViewModel : ViewModel() {
    private val _comments = MutableLiveData<List<CommentResponse>>(emptyList())
    val comments: LiveData<List<CommentResponse>> = _comments

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    fun loadComments(postId: Long) {
        RetrofitInstance.commentService.getCommentsOnPost(postId)
            .enqueue(object : Callback<List<CommentResponse>> {
                override fun onResponse(
                    call: Call<List<CommentResponse>>,
                    response: Response<List<CommentResponse>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        _comments.value = response.body()
                        _error.value = null
                    } else {
                        _error.value = "Lỗi tải bình luận: ${response.code()}"
                    }
                }
                override fun onFailure(call: Call<List<CommentResponse>>, t: Throwable) {
                    _error.value = "Không tải được bình luận: ${t.localizedMessage}"
                }
            })
    }

    fun postComment(postId: Long, text: String) {
        RetrofitInstance.commentService.comment(text, postId)
            .enqueue(object : Callback<CommentResponse> {
                override fun onResponse(
                    call: Call<CommentResponse>,
                    response: Response<CommentResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val updated = _comments.value.orEmpty() + response.body()!!
                        _comments.value = updated
                        _error.value = null
                    } else {
                        _error.value = "Lỗi gửi bình luận: ${response.code()}"
                    }
                }
                override fun onFailure(call: Call<CommentResponse>, t: Throwable) {
                    _error.value = "Không thể gửi bình luận: ${t.localizedMessage}"
                }
            })
    }
}
