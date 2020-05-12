package com.example.awesomeproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import com.example.awesomeproject.courses.CoursesListActivity
import com.example.awesomeproject.messages.LatestMessagesActivity
import com.example.awesomeproject.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_user_profile.*

class UserProfileActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar

    companion object {
        var currentUser: User? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.profileToolbarTitle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fetchCurrentUser()

        chatButtonP.setOnClickListener {
            startActivity(Intent(this, LatestMessagesActivity::class.java))
        }

        coursesButtonP.setOnClickListener {
            startActivity(Intent(this, CoursesListActivity::class.java))
        }
    }

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) { }

            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
                usernameTextViewP.text = currentUser?.username
                Picasso.get().load(currentUser?.profileImageUrl).into(photoImageViewP)
            }
        })
    }

}
