package com.hoan.frontend.models.entities

data class Comment(
    val id: Int,
    val user_id: Int,
    val post_id: Int,
    val content: String,
    val created_at: String
)
