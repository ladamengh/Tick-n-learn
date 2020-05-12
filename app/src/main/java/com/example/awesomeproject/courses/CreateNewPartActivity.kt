package com.example.awesomeproject.courses

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.awesomeproject.R
import com.example.awesomeproject.models.PartOfCourse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_create_new_part.*
import java.util.*

class CreateNewPartActivity : AppCompatActivity() {

    val auth = FirebaseAuth.getInstance()
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_part)

        toolbar = findViewById(R.id.toolbar)

        val courseUid = intent.getStringExtra("courseUid")
        val courseTitle = intent.getStringExtra("courseTitle")

        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(courseTitle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        createPartButtonP.setOnClickListener {
            createNewPart(courseUid!!)
            startActivity(Intent(this, CreateQuestionsActivity::class.java))
        }
    }

    private fun createNewPart(courseUid: String) {
        val uid = UUID.randomUUID().toString()
        val ref = FirebaseDatabase.getInstance().getReference("/course/$courseUid/$uid")

        val titlePart = titleEditTextP.text.toString()
        val imagePartUrl = imagePartUrlEditTextP.text.toString()
        val textPart = textEditTextP.text.toString()
        val timeStamp = System.currentTimeMillis() / 1000

        if (titlePart.isEmpty() || textPart.isEmpty())
        {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
        } else {
        val coursePart = PartOfCourse(uid, imagePartUrl, titlePart, textPart, timeStamp)

        ref.setValue(coursePart)
            .addOnSuccessListener {
                val intent = Intent(this, CreateQuestionsActivity::class.java)
                intent.putExtra("courseUid", courseUid)
                intent.putExtra("courseTitle", titlePart)
                intent.putExtra("coursePartUid", uid)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
