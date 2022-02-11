package com.example.awesomeproject.auth

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.awesomeproject.R
import com.example.awesomeproject.SaveData
import com.example.awesomeproject.UserProfileActivity
import com.example.awesomeproject.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*

class SignUpActivity : AppCompatActivity() {

    val auth = FirebaseAuth.getInstance()
    private lateinit var toolbar: Toolbar
    private var selectedPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        setTools()

        registerButtonR.setOnClickListener {
            signUpUser()
        }

        alreadyHaveAccountTextView.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        photoButtonR.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
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
        titleToolbar.setText(R.string.registration)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            photoImageViewR.setImageBitmap(bitmap)
            photoButtonR.alpha = 0f
        }
    }

    private fun signUpUser() {
        val email = emailEditTextR.text.toString()
        val password = passwordEditTextR.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.allFields, Toast.LENGTH_SHORT).show()
        } else {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        uploadImageToFirebaseStorage()
                    } else {
                        Toast.makeText(this, R.string.resetError, Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    saveUserToFirebaseDatabase(it.toString())
                }
                    .addOnFailureListener {
                        Toast.makeText(this, R.string.resetError, Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, R.string.resetError, Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, usernameEditTextR.text.toString(), profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                val intent = Intent(this, UserProfileActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(this, R.string.resetError, Toast.LENGTH_SHORT).show()
            }
    }
}
