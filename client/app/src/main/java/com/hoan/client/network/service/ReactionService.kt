package com.hoan.client.network.service

import com.hoan.client.network.response.ReactionResponse
import retrofit2.Call
import retrofit2.http.*

interface ReactionService {

    @GET("reaction/post/{postId}")
    fun getReactionsOnPost(@Path("postId") postId: Long): Call<List<ReactionResponse>>

    @POST("reaction")
    fun react(
        @Query("reaction") reaction : String,
        @Query("post_id") postId: Long,
    ): Call<ReactionResponse>
}
