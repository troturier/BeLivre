package com.openclassrooms.belivre.models.apiModels

import com.google.gson.annotations.SerializedName

data class VolumeInfo (
    @SerializedName("title") val title:String,
    @SerializedName("subtitle") val subtitle:String,
    @SerializedName("publisher") val publisher:String,
    @SerializedName("publishedDate") val publishedDate:String,
    @SerializedName("imageLinks") val imageLinks: ImageLinks?,
    @SerializedName("authors") val authors:List<String>,
    @SerializedName("categories") val categories:List<String>,
    @SerializedName("description") val description:String)