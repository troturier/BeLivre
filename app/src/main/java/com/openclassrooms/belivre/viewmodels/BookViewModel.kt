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

class BookViewModel: ViewModel() {
    private var bookRepository = BookRepository()
    private var books : MutableLiveData<List<Book>> = MutableLiveData()
    private var book : MutableLiveData<Book> = MutableLiveData()
    private var exist : Boolean = false

    private val toastMessage = SingleLiveEvent<Int>()

    // save book to firebase
    fun addBook(book: Book){
        bookRepository.getBook(book.id!!).addSnapshotListener { value, _ ->
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

    // get realtime updates from firebase regarding saved books
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

    // get realtime updates from firebase regarding book
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

    fun checkBook(id: String): Boolean {
        bookRepository.getBook(id).addSnapshotListener(EventListener<DocumentSnapshot> { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@EventListener
            }
           exist = value!!.exists()
        })

        return exist
    }

    // delete a book from firebase
    fun deleteBook(book: Book){
        bookRepository.deleteBook(book).addOnFailureListener {
            Log.e(TAG,"Failed to delete Book")
        }
    }

    companion object {
        const val TAG = "BOOK_VIEW_MODEL"
    }
}