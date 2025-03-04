package com.hoan.client.network.request

data class UserRequest(
    var username: String,
    var fullName: String?,
    var email: String,
    var biography: String?,
    var location: String?
)