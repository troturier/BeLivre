package com.openclassrooms.belivre.models

data class User (
    var id: String? = null,
    var lastname: String? = null,
    var firstname: String? = null,
    var email: String? = null,
    var cityId: Int? = 0,
    var profilePicURL : String? = null  )