package com.hoan.client.network.response


data class ConversationDetailResponse(
    val id: Int,
    val type: String,
    val name: String,
    val messages: List<RecentMessages>,
    val members: List<Member>
)
