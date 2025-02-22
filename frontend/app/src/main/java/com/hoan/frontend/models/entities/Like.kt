package com.hoan.frontend.models.entities

data class Like(
    val id: Long,
    val user_id: Long,
    val post_id: Long,
    val created_at: String,
    val updated_at: String
)
