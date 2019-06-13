package com.openclassrooms.belivre.models

/**
 * BookReview Model
 * @property id String?
 * @property userId String?
 * @property bookId String?
 * @property displayName String?
 * @property profilePicUrl String?
 * @property rate Double?
 * @property content String?
 * @constructor
 */
data class BookReview (
    var id: String? = null,
    var userId: String? = null,
    var bookId: String? = null,
    var displayName : String? = null,
    var profilePicUrl : String? = null,
    var rate: Double? = null,
    var content: String? = null
)