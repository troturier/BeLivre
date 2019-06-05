package com.openclassrooms.belivre.models

import java.util.*

data class Message (
    val text: String? = null,
    val time: Date? = null,
    val senderId : String? = null)