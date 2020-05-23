package com.example.awesomeproject

import android.content.Intent
import com.example.awesomeproject.R
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.awesomeproject.SplashScreenActivity.Companion.currentUser
import com.example.awesomeproject.auth.LoginActivity
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
    private lateinit var saveData: SaveData

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        switchTheme()
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

    private fun switchTheme() {
        if (saveData.loadDarkModeState() == true) {
            switchDark.isChecked = true
        }
        switchDark.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                saveData.setDarkModeState(true)
            } else {
                saveData.setDarkModeState(false)
            }
            restartApplication()
        }
    }

    private fun setTools() {
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.profileToolbarTitle)

        val uid = intent.getStringExtra("userUid")
        val username = intent.getStringExtra("username")
        val profileImage = intent.getStringExtra("profileImage")

        if (uid == null) {
            val uidU = FirebaseAuth.getInstance().currentUser!!.uid
            fetchCurrentUser(uidU)
        } else {
            setUser(uid, username!!, profileImage!!)
        }
    }

    private fun setNavigation() {
        bottomNavigation.selectedItemId = R.id.profile
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

    private fun restartApplication() {
        val i = Intent(applicationContext, UserProfileActivity::class.java)
        startActivity(i)
        finish()
    }

    private fun setUser(uid: String, username: String, profileImage: String) {
        usernameTextViewP.text = username
        uidTextViewP.text = uid
        Picasso.get().load(profileImage).into(photoImageViewP)
    }

    private fun fetchCurrentUser(uid: String) {

        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) { }

            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
                usernameTextViewP.text = currentUser?.username
                uidTextViewP.text = currentUser?.uid
                Picasso.get().load(currentUser?.profileImageUrl).into(photoImageViewP)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_profile, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuSignOut -> signOut()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}
