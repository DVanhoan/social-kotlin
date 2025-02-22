package com.hoan.frontend.models.dto.user

data class PasswordRequest(
    val oldPassword: String?,
    val newPassword: String
)