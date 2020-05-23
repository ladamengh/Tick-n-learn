package com.example.awesomeproject.courses

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.awesomeproject.R
import com.example.awesomeproject.SaveData
import com.example.awesomeproject.models.Course
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_create_new_course.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*

class CreateNewCourseActivity : AppCompatActivity() {

    val auth = FirebaseAuth.getInstance()
    private lateinit var toolbar: Toolbar
    private lateinit var saveData: SaveData

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_course)

        setTools()

        createCourseButtonC.setOnClickListener { createNewCourse() }

        cancelText.setOnClickListener { cancelCreating() }
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
        supportActionBar?.setTitle(R.string.createCourseToolbarTitle)
    }

    private fun createNewCourse() {
        val uid = UUID.randomUUID().toString()
        val ref = FirebaseDatabase.getInstance().getReference("/course/$uid")

        val courseTitle = titleEditTextC.text.toString()
        val description = descriptionEditTextC.text.toString()
        val courseUrl = imageUrlEditTextC.text.toString()

        if (courseTitle.isEmpty() || description.isEmpty() || courseUrl.isEmpty())
        {
            Toast.makeText(this, R.string.allFields, Toast.LENGTH_SHORT).show()
        } else {
        val course = Course(uid, courseUrl, courseTitle, description)

        ref.setValue(course)
            .addOnSuccessListener {
                val intent = Intent(this, CreateNewPartActivity::class.java)
                intent.putExtra("courseUid", uid)
                intent.putExtra("courseTitle", courseTitle)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(this, R.string.resetError, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cancelCreating() {
        val intent = Intent(this, CoursesListActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}
