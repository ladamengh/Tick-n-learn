package com.example.awesomeproject.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.awesomeproject.R
import com.example.awesomeproject.SaveData
import com.example.awesomeproject.UserProfileActivity
import com.example.awesomeproject.courses.CoursesListActivity
import com.example.awesomeproject.messages.NewDialogActivity.Companion.USER_KEY
import com.example.awesomeproject.models.ChatMessage
import com.example.awesomeproject.models.LatestMessageRow
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_latest_messages.*
import kotlinx.android.synthetic.main.activity_latest_messages.bottomNavigation

class LatestMessagesActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var saveData: SaveData

    private val adapter = GroupAdapter<GroupieViewHolder>()
    private val latestMessagesMap = HashMap<String, ChatMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)

        listenForLatestMessages()
        setTools()
        setNavigation()
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
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.latestMessagesToolbarTitle)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        latestMessagesRecyclerView.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }

    private fun setNavigation() {
        bottomNavigation.selectedItemId = R.id.dialogs
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.profile -> {
                    startActivity(Intent(this, UserProfileActivity::class.java))
                }
                R.id.courses -> {
                    startActivity(Intent(this, CoursesListActivity::class.java))
                }
                R.id.dialogs -> {
                    startActivity(Intent(this, LatestMessagesActivity::class.java))
                }
            }
            true
        }
    }

    private fun listenForLatestMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")

        latestMessagesRecyclerView.adapter = adapter

        ref.addChildEventListener(object: ChildEventListener {

            override fun onChildAdded(onAddSnapshot: DataSnapshot, p1: String?) {
                val chatMessage = onAddSnapshot.getValue(ChatMessage::class.java) ?: return

                latestMessagesMap[onAddSnapshot.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }

            override fun onChildChanged(onChangeSnapshot: DataSnapshot, p1: String?) {
                val chatMessage = onChangeSnapshot.getValue(ChatMessage::class.java) ?: return

                latestMessagesMap[onChangeSnapshot.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) { }
            override fun onChildRemoved(p0: DataSnapshot) { }

            override fun onCancelled(error: DatabaseError) {
                throw error.toException()
            }
        })

        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(this, ChatLogActivity::class.java)
            val row = item as LatestMessageRow
            intent.putExtra(USER_KEY, row.chatPartnerUser)
            startActivity(intent)
        }
    }

    private fun refreshRecyclerViewMessages() {
        adapter.clear()
        latestMessagesMap.values.forEach {
            adapter.add(LatestMessageRow(it))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuNewMessage -> startActivity(Intent(this, NewDialogActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
