package com.openclassrooms.belivre.models

data class BookReview (
    var id: String? = null,
    var userId: String? = null,
    var bookId: String? = null,
    var displayName : String? = null,
    var profilePicUrl : String? = null,
    var rate: Double? = null,
    var content: String? = null
)