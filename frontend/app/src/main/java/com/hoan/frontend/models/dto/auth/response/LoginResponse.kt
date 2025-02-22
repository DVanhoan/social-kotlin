package com.hoan.frontend.models.dto.response

import com.hoan.frontend.models.entities.User

data class LoginResponse(
    val user: User,
    val token: String
)
