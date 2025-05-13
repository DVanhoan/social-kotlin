package com.hoan.client.network.response

import com.squareup.moshi.Json

data class ReactionResponse(
    @Json(name = "id")
    var id: Long,

    @Json(name = "user_id")
    var userId: Long,

    @Json(name = "post_id")
    var postId: Long,

    @Json(name = "reaction_type")
    val reaction_type: String,

    @Json(name = "reaction_time")
    val reactionTime: String,

    @Json(name = "user")
    var user: UserResponse?
)