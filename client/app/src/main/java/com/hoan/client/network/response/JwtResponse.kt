package com.hoan.client.network.response

import com.squareup.moshi.Json

data class JwtResponse(
    @Json(name = "user")
    var user: UserResponse?,

    @Json(name = "jwt")
    var jwt: String?
)