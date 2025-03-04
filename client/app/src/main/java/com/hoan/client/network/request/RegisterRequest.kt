package com.hoan.client.network.request

data class RegisterRequest(
    var username: String,
    var password: String,
    var fullName: String?,
    var email: String,
)
