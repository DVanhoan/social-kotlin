package com.hoan.client.network.service

import com.hoan.client.network.response.CommentResponse
import retrofit2.Call
import retrofit2.http.*

interface CommentService {

    @GET("comment/post/{postId}")
    fun getCommentsOnPost(@Path("postId") postId: Long): Call<List<CommentResponse>>

    @POST("comment")
    fun comment(
        @Query("comment") comment: String,
        @Query("post") post: Long
    ): Call<CommentResponse>
}
