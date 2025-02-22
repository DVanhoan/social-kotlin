package com.hoan.frontend.services

import com.hoan.frontend.models.dto.MessageResponse
import com.hoan.frontend.models.dto.post.CommentRequest
import com.hoan.frontend.models.entities.Comment
import com.hoan.frontend.models.entities.Post
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface PostService {

    @GET("posts")
    suspend fun getFeed(): Response<List<Post>>

    @Multipart
    @POST("posts/post")
    suspend fun uploadPost(
        @Part("description") description: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<Post>

    @DELETE("posts/{post_id}")
    suspend fun deletePost(@Path("post_id") postId: Long): Response<MessageResponse>

    @GET("posts/post/{post_id}")
    suspend fun getPostById(@Path("post_id") postId: Long): Response<Post>

    @GET("posts/like/{post_id}")
    suspend fun like(@Path("post_id") postId: Long): Response<MessageResponse>

    @GET("posts/unlike/{post_id}")
    suspend fun unlike(@Path("post_id") postId: Long): Response<MessageResponse>

    @POST("posts/comment")
    suspend fun insertComment(@Body commentRequest: CommentRequest): Response<Comment>
}
