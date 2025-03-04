package com.hoan.client.network.response

import androidx.room.Entity


@Entity
data class UserResponse(
    var id: Long,
    var username: String,
    var fullName: String?,
    var email: String,
    var biography: String?,
    var location: String?,
    var profilePicture: String?,
    var registration_date: String
)
