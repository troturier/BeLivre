package com.openclassrooms.belivre.controllers.activities

import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.openclassrooms.belivre.R
import com.openclassrooms.belivre.adapters.BookRecyclerViewAdapter
import com.openclassrooms.belivre.api.getBooks
import com.openclassrooms.belivre.models.Book
import com.openclassrooms.belivre.models.User
import com.openclassrooms.belivre.models.UserBook
import com.openclassrooms.belivre.models.apiModels.BookResults
import com.openclassrooms.belivre.viewmodels.BaseViewModelFactory
import com.openclassrooms.belivre.viewmodels.BookViewModel
import com.openclassrooms.belivre.viewmodels.UserBookViewModel
import kotlinx.android.synthetic.main.activity_search.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: BookRecyclerViewAdapter
    private var currentUser: FirebaseUser? = null
    private lateinit var user: User
    private var mAuth: FirebaseAuth? = null

    private val bookVM: BookViewModel by lazy {
        ViewModelProviders.of(this, BaseViewModelFactory { BookViewModel() }).get(BookViewModel::class.java)
    }

    private val userBookVM: UserBookViewModel by lazy {
        ViewModelProviders.of(this, BaseViewModelFactory { UserBookViewModel() }).get(UserBookViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolbar_search)

        user = intent.getSerializableExtra("user") as User
        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth?.currentUser

        linearLayoutManager = LinearLayoutManager(this)
        searchRV.layoutManager = linearLayoutManager

        configureSearchView()

    }

    private fun configureSearchView(){
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query!!.length >= 3) {
                    getBooks(object : Callback<BookResults> {
                        override fun onFailure(call: Call<BookResults>, t: Throwable) {
                            Log.e("testRetrofit", "ERROR")
                        }

                        override fun onResponse(call: Call<BookResults>, response: Response<BookResults>) {
                            val books = response.body()!!.items
                            val booksList = mutableListOf<Book>()
                            Log.i("testRetrofit", "Here is the response :")
                            if (books.isNotEmpty()) {
                                for (b in books) {
                                    val book = Book(b.id, b.volumeInfo.title, b.volumeInfo.authors, b.volumeInfo.categories ,b.volumeInfo.publisher, b.volumeInfo.publishedDate, b.volumeInfo.subtitle, b.volumeInfo.imageLinks?.thumbnail, b.volumeInfo.description, 0.0)
                                    booksList.add(book)
                                }
                                adapter = BookRecyclerViewAdapter(booksList){
                                        item:Book, _: Int ->
                                    bookVM.addBook(item)
                                    val userBook = UserBook(
                                        user.id + item.id,
                                        item.id,
                                        user.id,
                                        getString(R.string.profile_display_name, user.firstname, user.lastname?.substring(0,1)),
                                        user.profilePicURL,
                                        user.cityId,
                                        user.cityName,
                                        item.title,
                                        item.authors,
                                        item.categories,
                                        item.publisher,
                                        item.coverUrl,
                                        1
                                        )
                                    userBookVM.addUserBook(userBook)
                                    finish()
                                }
                                searchRV.adapter = adapter
                                adapter.notifyDataSetChanged()
                            }
                        }
                    }, query)
                }
                return false
            }
        })
        searchView.setIconifiedByDefault(false)
        searchView.requestFocus()
    }

    companion object
}
