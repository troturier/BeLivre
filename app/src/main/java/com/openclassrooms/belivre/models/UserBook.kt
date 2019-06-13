package com.openclassrooms.belivre.models

/**
 * UserBook Model
 * @property id String?
 * @property bookId String?
 * @property userId String?
 * @property userDisplayName String?
 * @property userPicUrl String?
 * @property cityId String?
 * @property cityName String?
 * @property title String?
 * @property authors List<String>?
 * @property categories List<String>?
 * @property publisher String?
 * @property coverUrl String?
 * @property status Int?
 * @property lastBorrowerId String?
 * @property lastBorrowerDisplayName String?
 * @property lastBorrowerPicUrl String?
 * @property lastBorrowedOn String?
 * @property requestSenderId String?
 * @property requestSenderDisplayName String?
 * @property requestSenderPicUrl String?
 * @constructor
 */
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
