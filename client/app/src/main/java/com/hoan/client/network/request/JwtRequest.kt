package com.hoan.client.network.request

data class JwtRequest(
    var usernameOrEmail: String,
    var password: String
)