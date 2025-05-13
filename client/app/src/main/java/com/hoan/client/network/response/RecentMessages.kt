package com.hoan.client.network.response

import com.squareup.moshi.Json

data class RecentMessages(
    @Json(name = "id")
    val id: Int,

    @Json(name = "sender")
    val sender: UserResponse,

    @Json(name = "content")
    val content: String?,

    @Json(name = "image_url")
    val image_url: String?,

    @Json(name = "created_at")
    val createdAt: String,

    @Json(name = "isSender")
    val isSender: Boolean,

    @Json(name = "conversation_id")
    val conversation_id: Int,
)
