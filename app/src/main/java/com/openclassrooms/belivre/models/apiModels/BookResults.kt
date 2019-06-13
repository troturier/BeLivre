package com.openclassrooms.belivre.models.apiModels

import com.google.gson.annotations.SerializedName

/**
 * BookResults Model
 * For Google Books API response
 * @property items List<BookResult>
 * @constructor
 */
data class BookResults(
    @SerializedName("items") val items: List<BookResult>
)