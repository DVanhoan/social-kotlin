package com.hoan.client.network.response

import com.squareup.moshi.Json


data class UserResponse(
    @Json(name = "id")
    var id: Long,

    @Json(name = "username")
    var username: String,

    @Json(name = "fullName")
    var fullName: String?,

    @Json(name = "email")
    var email: String,

    @Json(name = "biography")
    var biography: String?,

    @Json(name = "location")
    var location: String?,

    @Json(name = "profile_picture")
    var profilePicture: String?,

    @Json(name = "registration_date")
    var registration_date: String
)
