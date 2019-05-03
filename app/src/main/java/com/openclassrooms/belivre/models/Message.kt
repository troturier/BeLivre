package com.openclassrooms.belivre.models

data class Message (
    var id: String? = null,
    var senderId: String? = null,
    var receiverId: String? = null,
    var content: String? = null,
    var date: String? = null,
    var read: Boolean? = null
)