package com.hoan.client.network.service

import com.hoan.client.network.response.ChatResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface MessagesService  {

    @GET("conversation/{conversationId}")
    fun getChat(@Path("conversationId") conversationId: Long): ChatResponse
}