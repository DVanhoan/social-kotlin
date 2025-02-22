package com.hoan.frontend.models.dto.user

data class UpdateRequest(
    val name: String?,
    val username: String?,
    val email: String?,
    val description: String?
)