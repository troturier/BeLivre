package com.openclassrooms.belivre.models.apiModels

import com.google.gson.annotations.SerializedName

/**
 * VolumeInfo Model
 * For Google Books API response
 * @property title String
 * @property subtitle String
 * @property publisher String
 * @property publishedDate String
 * @property imageLinks ImageLinks?
 * @property authors List<String>
 * @property categories List<String>
 * @property description String
 * @constructor
 */
data class VolumeInfo (
    @SerializedName("title") val title:String,
    @SerializedName("subtitle") val subtitle:String,
    @SerializedName("publisher") val publisher:String,
    @SerializedName("publishedDate") val publishedDate:String,
    @SerializedName("imageLinks") val imageLinks: ImageLinks?,
    @SerializedName("authors") val authors:List<String>,
    @SerializedName("categories") val categories:List<String>,
    @SerializedName("description") val description:String)