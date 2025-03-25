package com.hoan.client.network.response

import androidx.room.Entity
import com.squareup.moshi.Json

@Entity
data class CommentResponse(
    @Json(name = "id")
    var id: Long,

    @Json(name = "user_id")
    var userId: Long,

    @Json(name = "post_id")
    var postId: Long,

    @Json(name = "text")
    val text: String,

    @Json(name = "comment_time")
    val commentTime: String,

    @Json(name = "user")
    var user: UserResponse?
)