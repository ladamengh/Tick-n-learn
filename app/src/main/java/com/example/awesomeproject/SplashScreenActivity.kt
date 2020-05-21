package com.example.awesomeproject

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.example.awesomeproject.auth.MainActivity
import com.example.awesomeproject.courses.CoursesListActivity
import com.example.awesomeproject.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var topAnim: Animation
    private lateinit var bottomAnim: Animation
    private var SPLASH_SCREEN: Long = 1000
    private lateinit var saveData: SaveData

    companion object {
        var currentUser: User? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        saveData = SaveData(this)
        if (saveData.loadDarkModeState() == true) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation)
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation)

        splashImageView.setAnimation(topAnim)
        splashName.animation = bottomAnim

        val cm = baseContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetworkInfo

        if (networkInfo != null && networkInfo.isConnected) {
            Toast.makeText(baseContext, "Интернет", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(baseContext, "Нет Интернета", Toast.LENGTH_LONG).show()
        }

        Handler().postDelayed(object: Runnable {
            override fun run() {
                if (networkInfo != null && networkInfo.isConnected) {
                    checkUsersSession()
                } else {
                    Toast.makeText(baseContext, "Нет Интернета", Toast.LENGTH_LONG).show()
                    startActivity(Intent(baseContext, CoursesListActivity::class.java))
                }
            }
        }, SPLASH_SCREEN)
    }

    private fun checkUsersSession() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } else {
            fetchCurrentUser(uid)
        }
    }

    private fun fetchCurrentUser(uid: String) {

        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) { }

            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
                val intent = Intent(baseContext, UserProfileActivity::class.java)
                intent.putExtra("username", currentUser?.username)
                intent.putExtra("userUid", uid)
                intent.putExtra("profileImage", currentUser?.profileImageUrl)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        })
    }
}
