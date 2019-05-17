package com.openclassrooms.belivre.models

data class UserWishlistBook(
        var id: String? = null,
        var userId: String? = null,
        var bookId: String? = null,
        var title: String? = null,
        var authors: List<String>? = emptyList(),
        var categories: List<String>? = emptyList(),
        var publisher: String? = null,
        var coverUrl: String? = null)