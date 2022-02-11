package com.example.awesomeproject

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.example.awesomeproject.auth.LoginActivity
import com.example.awesomeproject.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SplashScreenActivity : AppCompatActivity() {

    private var splashScreenTime: Long = 2000
    private lateinit var saveData: SaveData

    companion object {
        var currentUser: User? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("Splash", "onCreate method")
        setTheme()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        checkConnection()
    }

    private fun setTheme() {
        saveData = SaveData(this)
        if (saveData.loadDarkModeState() == true) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }
    }

    private fun checkConnection() {
        val cm = baseContext
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetworkInfo

        Handler().postDelayed({
            if (networkInfo != null && networkInfo.isConnected) {
                checkUsersSession()
            } else {
                Toast.makeText(baseContext, R.string.offline, Toast.LENGTH_LONG).show()
                checkUsersSession()
            }
        }, splashScreenTime)
    }

    private fun checkUsersSession() {
        Log.d("Splash", "Checking user's session")
        val uid = FirebaseAuth.getInstance().uid

        if (uid == null) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } else {
            fetchCurrentUser(uid)
        }
    }

    private fun fetchCurrentUser(uid: String) {

        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.keepSynced(true)

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(fetchUserSnapshot: DataSnapshot) {
                currentUser = fetchUserSnapshot.getValue(User::class.java)
                val intent = Intent(baseContext, UserProfileActivity::class.java)
                intent.putExtra("username", currentUser?.username)
                intent.putExtra("userUid", uid)
                intent.putExtra("profileImage", currentUser?.profileImageUrl)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            override fun onCancelled(error: DatabaseError) {
                throw error.toException()
            }
        })
    }
}
