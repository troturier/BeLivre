package com.openclassrooms.belivre.models

data class UserBook (
    var id: String? = null,
    var bookId: String? = null,
    var userId: String? = null,
    var userDisplayName: String? = null,
    var userPicUrl: String? = null,
    var cityId: String? = null,
    var cityName: String? = null,
    var title: String? = null,
    var authors: List<String>? = emptyList(),
    var categories: List<String>? = emptyList(),
    var publisher: String? = null,
    var coverUrl: String? = null,
    var status: Int? = null,
    var lastBorrowerId: String? = null,
    var lastBorrowerDisplayName: String? = null,
    var lastBorrowerPicUrl: String? = null,
    var lastBorrowedOn: String? = null,
    var requestSenderId: String? = null,
    var requestSenderDisplayName: String? = null,
    var requestSenderPicUrl: String? = null)
