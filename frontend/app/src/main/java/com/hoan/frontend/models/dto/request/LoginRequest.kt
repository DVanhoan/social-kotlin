package com.hoan.frontend.models.dto.request

data class LoginRequest(
    val usernameOrEmail: String,
    val password: String
)
