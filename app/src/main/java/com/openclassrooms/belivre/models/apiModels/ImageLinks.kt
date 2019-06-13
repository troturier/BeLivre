package com.openclassrooms.belivre.models.apiModels

import com.google.gson.annotations.SerializedName

/**
 * ImageLinks Model
 * For Google Books API response
 * @property thumbnail String?
 * @constructor
 */
data class ImageLinks (
    @SerializedName("thumbnail") val thumbnail:String? = ""
)