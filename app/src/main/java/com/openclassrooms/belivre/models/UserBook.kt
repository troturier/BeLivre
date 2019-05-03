package com.openclassrooms.belivre.models

data class UserBook (
    var id: String? = null,
    var bookId: String? = null,
    var userId: String? = null,
    var lastBorrowerId: String? = null,
    var lastBorrowedOn: String? = null,
    var status: Int? = null )