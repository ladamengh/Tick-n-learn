package com.example.awesomeproject.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doOnTextChanged
import com.example.awesomeproject.R
import com.example.awesomeproject.SaveData
import com.example.awesomeproject.models.User
import com.example.awesomeproject.models.UserItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*

class NewMessageActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var saveData: SaveData

    companion object {
        val USER_KEY = "USER_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        saveData = SaveData(this)
        if (saveData.loadDarkModeState() == true) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.newMessageToolbarTitle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fetchUsers()

        searchText.doOnTextChanged { text, start, count, after ->

            val searchText = searchText.text.toString().trim()

            searchUser(searchText)
        }
    }

    private fun fetchUsers() {

        val ref = FirebaseDatabase.getInstance().getReference("/users")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                val adapter = GroupAdapter<GroupieViewHolder>()

                p0.children.forEach {
                    val user = it.getValue(User::class.java)
                    adapter.add(UserItem(user!!))
                }

                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as UserItem
                    val intent = Intent(view.context, ChatLogActivity::class.java)
                    intent.putExtra(USER_KEY, userItem.user)
                    startActivity(intent)

                    finish()
                }

                recyclerViewNewMessage.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun searchUser(searchText: String) {
        val adapter = GroupAdapter<GroupieViewHolder>()
        if (searchText.isEmpty()) {
            fetchUsers()
        } else {
            val ref = FirebaseDatabase.getInstance().getReference("/users")
            val searchQuery = ref.orderByChild("username").startAt(searchText).endAt("$searchText\uf8ff")

            searchQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {

                    p0.children.forEach {
                        val user = it.getValue(User::class.java)
                        adapter.add(UserItem(user!!))
                    }

                    adapter.setOnItemClickListener { item, view ->
                        val userItem = item as UserItem
                        val intent = Intent(view.context, ChatLogActivity::class.java)
                        intent.putExtra(USER_KEY, userItem.user)
                        startActivity(intent)

                        finish()
                    }

                    recyclerViewNewMessage.adapter = adapter
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
        }
    }
}
