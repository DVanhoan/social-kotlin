package com.hoan.client.network.response

data class Message(
    val id: Int,
    val content: String,
    val sender_id: Int,
    val conversation_id: Int,
    val message_type: String,
)
