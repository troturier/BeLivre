package com.openclassrooms.belivre.models.apiModels

import com.google.gson.annotations.SerializedName

data class BookResult(
    @SerializedName("id") val id: String,
    @SerializedName("volumeInfo") val volumeInfo: VolumeInfo
)