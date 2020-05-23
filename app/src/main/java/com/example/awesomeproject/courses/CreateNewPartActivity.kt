package com.example.awesomeproject.courses

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.awesomeproject.R
import com.example.awesomeproject.SaveData
import com.example.awesomeproject.models.PartOfCourse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_create_new_part.*
import java.util.*

class CreateNewPartActivity : AppCompatActivity() {

    val auth = FirebaseAuth.getInstance()
    private lateinit var toolbar: Toolbar
    private lateinit var saveData: SaveData

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_part)

        setTools()

        createPartButtonP.setOnClickListener {
            createNewPart()
            startActivity(Intent(this, CreateQuestionsActivity::class.java))
        }
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

        val courseTitle = intent.getStringExtra("courseTitle")

        setSupportActionBar(toolbar)
        supportActionBar?.title = courseTitle
    }

    private fun createNewPart() {
        val courseUid = intent.getStringExtra("courseUid") ?: ""
        val uid = UUID.randomUUID().toString()
        val ref = FirebaseDatabase.getInstance().getReference("/course/$courseUid/parts/$uid")

        val titlePart = titleEditTextP.text.toString()
        val imagePartUrl = imagePartUrlEditTextP.text.toString()
        val textPart = textEditTextP.text.toString()

        if (titlePart.isEmpty() || textPart.isEmpty())
        {
            Toast.makeText(this, R.string.allFields, Toast.LENGTH_SHORT).show()
        } else {
        val coursePart = PartOfCourse(uid, imagePartUrl, titlePart, textPart)

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
                Toast.makeText(this, R.string.resetError, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
