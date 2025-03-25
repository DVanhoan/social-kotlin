package com.hoan.client.network.request

data class CreateConversationRequest(
    val name: String,
    val type: String,
    val members: List<Int>
)
