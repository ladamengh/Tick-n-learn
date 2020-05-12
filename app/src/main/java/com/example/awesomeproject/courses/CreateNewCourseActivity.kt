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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_course)

        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.createCourseToolbarTitle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        createCourseButtonC.setOnClickListener {
            createNewCourse()
        }

        cancelTextViewC.setOnClickListener {
            startActivity(Intent(this, CoursesListActivity::class.java))
        }
    }

    private fun createNewCourse() {
        val uid = UUID.randomUUID().toString()
        val ref = FirebaseDatabase.getInstance().getReference("/course/$uid")

        val courseTitle = titleEditTextC.text.toString()
        val description = descriptionEditTextC.text.toString()
        val courseUrl = imageUrlEditTextC.text.toString()

        if (courseTitle.isEmpty() || description.isEmpty() || courseUrl.isEmpty())
        {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
