package com.hoan.frontend.models.dto.post

data class CommentRequest(
    val post_id: Long,
    val comment: String
)