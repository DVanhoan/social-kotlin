package com.hoan.frontend.models.entities

data class Post(
    val id: Long,
    val user_id: Long,
    val image: String?,
    val description: String?,
    val created_at: String?,
    val updated_at: String?,
    val user: User?,
    val likes_count: Int,
    val comments_count: Int,
    val is_liked: Boolean
)