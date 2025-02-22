package com.hoan.frontend.models.entities

data class Post(
    val id: Int,
    val image: String,
    val description: String,
    val author: String,
    val created_at: String
)
