package com.hoan.client.network.service

import com.hoan.client.network.request.ReactRequest
import com.hoan.client.network.response.ReactionResponse
import retrofit2.Call
import retrofit2.http.*

interface ReactionService {

    @GET("reaction/post/{postId}")
    fun getReactionsOnPost(@Path("postId") postId: Long): Call<List<ReactionResponse>>

    @POST("reaction")
    fun react(
        @Body body: ReactRequest
    ): Call<ReactionResponse>
}
