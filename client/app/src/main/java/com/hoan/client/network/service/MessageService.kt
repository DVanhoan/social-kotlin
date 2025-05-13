package com.hoan.client.network.service

import com.hoan.client.network.request.CreateConversationRequest
import com.hoan.client.network.request.SendMessageRequest
import com.hoan.client.network.response.ConversationDetailResponse
import com.hoan.client.network.response.ConversationsResponse
import com.hoan.client.network.response.CreateConversationResponse
import com.hoan.client.network.response.RecentMessages
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.Call

interface MessageService  {

    @GET("conversation")
    fun getConversations(): Call<ConversationsResponse>

    @GET("conversation/{conversationId}")
    fun getConversationDetail(
        @Path("conversationId") conversationId: Long
    ): Call<ConversationDetailResponse>

    @POST("conversation")
    fun createConversation(
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Call<CreateConversationResponse>


    @Multipart
    @POST("message/send")
    fun sendMessageWithTextAndImage(
        @Part("conversation_id") conversationId: RequestBody,
        @Part("content") text: RequestBody,
        @Part file: MultipartBody.Part
    ): Call<RecentMessages>

    @POST("message/send")
    fun sendTextMessage(
        @Body request: SendMessageRequest
    ): Call<RecentMessages>

}