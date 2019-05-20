package com.openclassrooms.belivre.models

import java.io.Serializable

data class User (
    var id: String? = null,
    var lastname: String? = null,
    var firstname: String? = null,
    var email: String? = null,
    var cityId: String? = null,
    var cityName: String? = null,
    var profilePicURL : String? = null  ) : Serializable