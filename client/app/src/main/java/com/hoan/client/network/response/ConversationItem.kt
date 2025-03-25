package com.hoan.client.network.response

data class ConversationItem(
    val id: Int,
    val name: String,
    val last_message: String?,
    val last_message_time: String?,
    val isSender: Boolean,
    val other_participant: Participant?
)
