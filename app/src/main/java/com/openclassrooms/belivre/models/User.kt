package com.openclassrooms.belivre.models

data class User (
    var id: String? = null,
    var lastname: String? = null,
    var firstname: String? = null,
    var email: String? = null,
    var cityId: String? = "",
    var profilePicURL : String? = null  )