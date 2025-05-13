package com.hoan.client.network.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Notification(
    val id: Long,
    val type: String,
    val data: Map<String, Any>,
    val read_at: String?,
    val created_at: String
)

