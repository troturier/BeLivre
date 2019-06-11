package com.openclassrooms.belivre.api

import com.openclassrooms.belivre.models.apiModels.BookResults
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * ServiceEndPoint interface for Google Books API queries
 */
interface IServiceEndPoint {
    @GET("/books/v1/volumes")
    fun getBooks(@Query("q") query: String): Call<BookResults>
}