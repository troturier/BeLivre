package com.openclassrooms.belivre.models

data class BookUser (
    var id: String? = null,
    var bookId: String? = null,
    var userId: String? = null,
    var status: Int? = null )