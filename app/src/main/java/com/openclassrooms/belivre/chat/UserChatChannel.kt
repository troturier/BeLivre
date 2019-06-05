package com.openclassrooms.belivre.chat

import java.util.*

data class UserChatChannel (
    val channelId: String? = null,
    val userId: String? = null,
    val profilePic: String? = null,
    val displayName: String? = null,
    val lastMessage: String? = null,
    val seen: Boolean = true,
    val time: Date? = null
)