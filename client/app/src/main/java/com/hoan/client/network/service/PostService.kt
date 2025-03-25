package com.hoan.client.network.service

import com.hoan.client.network.response.PostResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface PostService {

    @GET("posts/post/friends")
    fun getPostsFromFriends(): Call<List<PostResponse>>

    @GET("posts/post/all")
    fun getAllPosts(): Call<List<PostResponse>>

    @Multipart
    @POST("posts/post/create")
    fun createPost(
        @Part mainPhoto: MultipartBody.Part?,
        @Part("content") content: RequestBody,
        @Part("location") location: RequestBody,
    ): Call<PostResponse>

    @PATCH("posts/post/{postId}")
    fun addDescription(
        @Path("postId") postId: Long,
        @Query("description") description: String
    ): Call<PostResponse>

    @GET("posts/post/image/{filename}")
    fun getImageUrl(@Path("filename") filename: String): Call<ResponseBody>
}
