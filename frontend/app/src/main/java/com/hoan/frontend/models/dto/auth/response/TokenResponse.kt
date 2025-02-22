package com.hoan.frontend.models.dto.auth.response

data class TokenResponse(
    val token: String,
    val refreshToken: String,
    val message: String
)