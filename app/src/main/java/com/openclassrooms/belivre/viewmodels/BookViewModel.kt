package com.openclassrooms.belivre.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.models.Book
import com.openclassrooms.belivre.repositories.BookRepository
import com.openclassrooms.belivre.utils.SingleLiveEvent

/**
 * Book ViewModel
 */
class BookViewModel: ViewModel() {
    private var bookRepository = BookRepository()
    private var books : MutableLiveData<List<Book>> = MutableLiveData()
    private var book : MutableLiveData<Book> = MutableLiveData()

    private val toastMessage = SingleLiveEvent<Int>()

    /**
     * Saves Book object in Firestore
     */
    fun addBook(book: Book){
        bookRepository.getBook(book.id!!).get().addOnSuccessListener { value ->
            if (!value!!.exists()) {
                bookRepository.addBook(book)
                    .addOnFailureListener {
                        toastMessage.value = R.string.update_fail
                    }
                    .addOnSuccessListener {
                        toastMessage.value = R.string.book_updated_success
                    }
            }
        }
    }

    /**
     * Update a Book rating in Firestore
     */
    fun updateBookRating(book: Book){
        bookRepository.updateBookRating(book)
            .addOnFailureListener {
                toastMessage.value = R.string.update_fail
            }
            .addOnSuccessListener {
                toastMessage.value = R.string.book_updated_success
            }
    }

    /**
     * Retrieves all Books from Firestore
     */
    fun getBooks(): LiveData<List<Book>>{
        bookRepository.getBooks().addSnapshotListener(EventListener<QuerySnapshot> { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                books.value = null
                return@EventListener
            }

            val bookList : MutableList<Book> = mutableListOf()
            for (doc in value!!) {
                val book = doc.toObject(Book::class.java)
                bookList.add(book)
            }
            books.value = bookList
        })

        return books
    }

    /**
     * Retrieves one Book from Firestore
     */
    fun getBook(id: String): LiveData<Book> {
        bookRepository.getBook(id).addSnapshotListener(EventListener<DocumentSnapshot> { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                books.value = null
                return@EventListener
            }
            book.value = value!!.toObject(Book::class.java)
        })

        return book
    }

    /**
     * Deletes one Book from Firestore
     */
    fun deleteBook(book: Book){
        bookRepository.deleteBook(book).addOnFailureListener {
            Log.e(TAG,"Failed to delete Book")
        }
    }

    companion object {
        const val TAG = "BOOK_VIEW_MODEL"
    }
}