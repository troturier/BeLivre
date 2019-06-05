package com.openclassrooms.belivre.chat

data class ChatChannel(
    val userIds: MutableList<String> = mutableListOf()
)