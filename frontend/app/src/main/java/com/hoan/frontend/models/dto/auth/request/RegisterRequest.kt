package com.hoan.frontend.models.dto.auth.request

data class RegisterRequest(
    val name: String,
    val username: String,
    val email: String,
    val password: String
)