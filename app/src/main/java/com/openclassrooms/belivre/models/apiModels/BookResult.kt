package com.openclassrooms.belivre.models.apiModels

import com.google.gson.annotations.SerializedName

/**
 * BookResult Model
 * For Google Books API response
 * @property id String
 * @property volumeInfo VolumeInfo
 * @constructor
 */
data class BookResult(
    @SerializedName("id") val id: String,
    @SerializedName("volumeInfo") val volumeInfo: VolumeInfo
)