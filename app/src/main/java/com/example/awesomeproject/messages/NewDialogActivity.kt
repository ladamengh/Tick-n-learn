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
import kotlinx.android.synthetic.main.activity_new_message.titleToolbar
import java.util.*

class NewDialogActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var saveData: SaveData

    companion object {
        const val USER_KEY = "USER_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        fetchUsers()
        setTools()

        searchText.doOnTextChanged { _, _, _, _ ->
            searchUser(searchText.text.toString().trim())
        }

        goBackMessage.setOnClickListener {
            startActivity(Intent(this, LatestMessagesActivity::class.java))
        }
    }

    private fun setTheme() {
        saveData = SaveData(this)
        if (saveData.loadDarkModeState() == true) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }
    }

    private fun setTools() {
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        titleToolbar.setText(R.string.newMessageToolbarTitle)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }


    private fun fetchUsers() {

        val ref = FirebaseDatabase.getInstance().getReference("/users")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(userSnapshot: DataSnapshot) {

                val adapter = GroupAdapter<GroupieViewHolder>()

                userSnapshot.children.forEach {
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

            override fun onCancelled(error: DatabaseError) {
                throw error.toException()
            }
        })
    }

    private fun searchUser(searchText: String) {
        val adapter = GroupAdapter<GroupieViewHolder>()

        if (searchText.isEmpty()) {
            fetchUsers()
        } else {
            val ref = FirebaseDatabase.getInstance().getReference("/users")
            val searchQuery = ref
                .orderByChild("username")
                .startAt(searchText.toUpperCase(Locale.ROOT))
                .endAt("${searchText.toLowerCase(Locale.ROOT)}\uf8ff")

            searchQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(userSearchSnapshot: DataSnapshot) {

                    userSearchSnapshot.children.forEach {
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
                override fun onCancelled(error: DatabaseError) {
                    throw error.toException()
                }
            })
        }
    }
}
