package com.openclassrooms.belivre.models

/**
 * City Model
 * @property id String?
 * @property name String?
 * @property lat String?
 * @property long String?
 * @property availabeUserBooks Int?
 * @constructor
 */
data class City (var id: String? = null,
                 var name: String? = null,
                 var lat: String? = null,
                 var long: String? = null,
                 var availabeUserBooks: Int? = 0)