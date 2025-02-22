package com.hoan.frontend.models.entities

data class Like(
    val id: Int,
    val user_id: Int,
    val post_id: Int,
    val created_at: String
)
