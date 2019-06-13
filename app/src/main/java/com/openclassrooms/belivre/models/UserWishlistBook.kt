package com.openclassrooms.belivre.models

/**
 * UserWishlist Model
 * @property id String?
 * @property userId String?
 * @property bookId String?
 * @property title String?
 * @property authors List<String>?
 * @property categories List<String>?
 * @property publisher String?
 * @property coverUrl String?
 * @constructor
 */
data class UserWishlistBook(
        var id: String? = null,
        var userId: String? = null,
        var bookId: String? = null,
        var title: String? = null,
        var authors: List<String>? = emptyList(),
        var categories: List<String>? = emptyList(),
        var publisher: String? = null,
        var coverUrl: String? = null)