package com.openclassrooms.belivre.models.apiModels

import com.google.gson.annotations.SerializedName

data class BookResults(
    @SerializedName("items") val items: List<BookResult>
)