package com.hoan.frontend.models.entities

data class Comment(
    val id: Long,
    val post_id: Long,
    val user_id: Long,
    val comment: String,
    val created_at: String,
    val updated_at: String
)
