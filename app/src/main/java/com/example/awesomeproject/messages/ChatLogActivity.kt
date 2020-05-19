package com.example.awesomeproject.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.awesomeproject.R
import com.example.awesomeproject.SaveData
import com.example.awesomeproject.SplashScreenActivity.Companion.currentUser
import com.example.awesomeproject.models.ChatFromItem
import com.example.awesomeproject.models.ChatMessage
import com.example.awesomeproject.models.ChatToItem
import com.example.awesomeproject.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*

class ChatLogActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private var toUser: User? = null
    private lateinit var saveData: SaveData

    val instance = FirebaseDatabase.getInstance()
    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {

        saveData = SaveData(this)
        if (saveData.loadDarkModeState() == true) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        toolbar = findViewById(R.id.toolbar)

        chatLogRecyclerView.adapter = adapter

        toUser = intent.getParcelableExtra(NewMessageActivity.USER_KEY)

        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(toUser?.username)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        listenForMessages()

        sendButtonChatLog.setOnClickListener {
            sendMessage()
        }
    }

    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = instance.getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(p0: DatabaseError) { }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) { }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) { }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)

                if (chatMessage != null) {
                    if (chatMessage.fromID == FirebaseAuth.getInstance().uid) {
                        adapter.add(ChatFromItem(chatMessage.text, currentUser!!))
                    } else {
                        adapter.add(ChatToItem(chatMessage.text, toUser!!))
                    }
                }

                chatLogRecyclerView.scrollToPosition(adapter.itemCount - 1)
            }

            override fun onChildRemoved(p0: DataSnapshot) { }
        })
    }

    private fun sendMessage() {
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val text = messageEditTextChatLog.text.toString()
        val fromId = FirebaseAuth.getInstance().uid
        val toId = user?.uid

        val reference = instance.getReference("/user-messages/$fromId/$toId").push()
        val toReference = instance.getReference("/user-messages/$toId/$fromId").push()
        val latestMessageReference = instance.getReference("/latest-messages/$fromId/$toId")
        val toLatestMessageReference = instance.getReference("/latest-messages/$toId/$fromId")

        val chatMessage = ChatMessage(reference.key!!, text,
            fromId!!, toId!!, System.currentTimeMillis() / 1000) // time in seconds

        reference.setValue(chatMessage)
            .addOnSuccessListener {
                messageEditTextChatLog.text.clear()
                chatLogRecyclerView.scrollToPosition(adapter.itemCount - 1)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Что-то пошло не так", Toast.LENGTH_SHORT).show()
            }
        toReference.setValue(chatMessage)
        latestMessageReference.setValue(chatMessage)
        toLatestMessageReference.setValue(chatMessage)
    }
}
