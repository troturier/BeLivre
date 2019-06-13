package com.openclassrooms.belivre.models

import java.io.Serializable

/**
 * User Model
 * @property id String?
 * @property lastname String?
 * @property firstname String?
 * @property email String?
 * @property cityId String?
 * @property cityName String?
 * @property profilePicURL String?
 * @property registrationTokens MutableList<String>
 * @constructor
 */
data class User (
    var id: String? = null,
    var lastname: String? = null,
    var firstname: String? = null,
    var email: String? = null,
    var cityId: String? = null,
    var cityName: String? = null,
    var profilePicURL : String? = null,
    val registrationTokens: MutableList<String> = mutableListOf()) : Serializable