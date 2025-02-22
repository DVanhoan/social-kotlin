package com.hoan.frontend.models.entities

data class User(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val pic: String?
)