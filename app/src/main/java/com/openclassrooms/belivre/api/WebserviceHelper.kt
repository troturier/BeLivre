package com.openclassrooms.belivre.api

import com.openclassrooms.belivre.models.apiModels.BookResults
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Function used to retrieve books from the Google Books API using a string query
 */
fun getBooks(callback: Callback<BookResults>, query:String){
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.googleapis.com")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        val service = retrofit.create(IServiceEndPoint::class.java)

        val booksRequest = service.getBooks(query)

        booksRequest.enqueue(callback)
    }