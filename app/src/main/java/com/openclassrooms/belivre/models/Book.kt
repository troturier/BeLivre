package com.openclassrooms.belivre.models

data class Book (
    var id: String? = null,
    var title: String? = null,
    var author: String? = null,
    var publisher: String? = null,
    var publishedDate: String? = null,
    var coverUrl: String? = null,
    var description : String? = null  )