package com.openclassrooms.belivre.models

data class Book (
    var id: String? = null,
    var title: String? = null,
    var authors: List<String>? = emptyList(),
    var categories: List<String>? = emptyList(),
    var publisher: String? = null,
    var publishedDate: String? = null,
    var subtitle: String? = null,
    var coverUrl: String? = null,
    var description : String? = null,
    var rating: Double = 0.0)