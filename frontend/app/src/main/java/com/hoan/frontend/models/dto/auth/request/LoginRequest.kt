package com.hoan.frontend.models.dto.auth.request

data class LoginRequest(
    val usernameOrEmail: String,
    val password: String
)
