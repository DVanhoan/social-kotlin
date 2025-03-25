package com.hoan.client.network.response

import androidx.room.Entity
import com.squareup.moshi.Json

@Entity
data class FriendshipResponse(
    @Json(name = "id")
    var id: Long,

    @Json(name = "user1_id")
    var user1Id: Long,

    @Json(name = "user2_id")
    var user2Id: Long,

    @Json(name = "status")
    var status: String,

    @Json(name = "since")
    var since: String
)