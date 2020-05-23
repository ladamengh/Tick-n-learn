package com.example.awesomeproject.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.awesomeproject.R
import com.example.awesomeproject.SaveData
import com.example.awesomeproject.UserProfileActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private var auth = FirebaseAuth.getInstance()
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setTools()

        loginButtonL.setOnClickListener {
            loginUser()
        }

        doNotHaveAccountTextView.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        forgotPasswordTextView.setOnClickListener {
            forgotPassword()
        }
    }

    private fun setTheme() {
        val saveData = SaveData(this)
        if (saveData.loadDarkModeState() == true) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }
    }

    private fun setTools() {
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.auth)
    }

    private fun loginUser() {
        val email = emailEditTextL.text.toString()
        val password = passwordEditTextL.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.allFields, Toast.LENGTH_SHORT).show()
        } else {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this, UserProfileActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    } else {
                        Toast.makeText(baseContext, R.string.authFailed, Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun forgotPassword() {
        val email = emailEditTextL.text.toString()

        if (email.isEmpty()) {
            Toast.makeText(baseContext, R.string.emptyEmail, Toast.LENGTH_SHORT).show()
        } else {
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(baseContext, R.string.resetEmail, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(baseContext, R.string.resetError, Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
