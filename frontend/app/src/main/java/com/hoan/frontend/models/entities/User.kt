package com.hoan.frontend.models.entities

data class User(
    val id: Long,
    val username: String?,
    val name: String?,
    val email: String?,
    val pic: String?,
    val description: String?,
    val created_at: String?,
    val updated_at: String?
)