package com.hoan.frontend.models.dto.auth.request

import com.hoan.frontend.models.entities.User

data class RefreshRequest(
    val token: String,
    val user: User
)