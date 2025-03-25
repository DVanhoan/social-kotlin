package com.hoan.client.network.service

import com.hoan.client.network.response.ReactionResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ReactionService {

    @GET("reaction/post/{postId}")
    fun getReactionsOnPost(@Path("postId") postId: Long): Call<List<ReactionResponse>>

    @GET("reaction/{filename}")
    fun getReactionImageUrl(@Path("filename") filename: String): Call<ResponseBody>

    @Multipart
    @POST("reaction")
    fun react(
        @Part reaction: MultipartBody.Part,
        @Part("post") postId: Long,
    ): Call<ReactionResponse>
}
