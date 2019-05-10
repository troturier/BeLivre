package com.openclassrooms.belivre.models

data class UserBook (
    var id: String? = null,
    var bookId: String? = null,
    var userId: String? = null,
    var title: String? = null,
    var authors: List<String>? = emptyList(),
    var categories: List<String>? = emptyList(),
    var publisher: String? = null,
    var coverUrl: String? = null,
    var lastBorrowerId: String? = null,
    var lastBorrowedOn: String? = null,
    var status: Int? = null )