package com.hoan.client.network.response

import com.squareup.moshi.Json

data class PostResponse(
    @Json(name = "id")
    val id: Long,

    @Json(name = "main_photo")
    val mainPhoto: String,

    @Json(name = "content")
    val content: String?,

    @Json(name = "location")
    val location: String?,

    @Json(name = "posting_time")
    val postingTime: String,

    @Json(name = "deleted")
    val deleted: Boolean,

    @Json(name = "user")
    var user: UserResponse?
)

