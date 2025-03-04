package com.hoan.client.network.response

import androidx.room.Entity
import com.squareup.moshi.Json

@Entity
data class PostResponse(
    @Json(name = "id")
    val id: Long,

    @Json(name = "userId")
    val userId: Long,

    @Json(name = "username")
    val username: String,

    @Json(name = "mainPhoto")
    val mainPhoto: String,

    @Json(name = "selfiePhoto")
    val selfiePhoto: String,

    @Json(name = "description")
    val description: String?,

    @Json(name = "location")
    val location: String?,

    @Json(name = "postingTime")
    val postingTime: String,

    @Json(name = "deleted")
    val deleted: Boolean
)
