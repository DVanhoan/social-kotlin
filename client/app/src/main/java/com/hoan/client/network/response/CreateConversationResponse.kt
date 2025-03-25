package com.hoan.client.network.response

data class CreateConversationResponse(
    val status: String,
    val conversation: ConversationDetailResponse
)
