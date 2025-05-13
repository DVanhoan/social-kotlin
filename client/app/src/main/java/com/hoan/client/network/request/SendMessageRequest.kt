package com.hoan.client.network.request

data class SendMessageRequest(
    val conversation_id : Long,
    val content: String?
)
