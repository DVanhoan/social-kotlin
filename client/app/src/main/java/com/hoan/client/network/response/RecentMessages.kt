package com.hoan.client.network.response

data class RecentMessages(
    val id: Int,
    val sender: UserResponse,
    val content: String,
    val created_at: String,
    val isSender: Boolean,
    val profile_picture: String
)
