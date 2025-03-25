package com.hoan.client.network.response

data class ConversationsResponse(
    val conversations: List<ConversationItem>,
    val recentConversationId: Int?,
    val recentMessages: List<RecentMessages>,
    val user: UserResponse
)
