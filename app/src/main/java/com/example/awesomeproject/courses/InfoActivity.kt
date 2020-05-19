package com.example.awesomeproject.courses

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.example.awesomeproject.R
import com.example.awesomeproject.SaveData
import com.example.awesomeproject.models.PartOfCourse
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_info.*

class InfoActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var courseUid: String
    private lateinit var coursePartUid: String
    private lateinit var coursePartTitle: String
    private var countQuestions: Long = 0
    private lateinit var saveData: SaveData

    override fun onCreate(savedInstanceState: Bundle?) {

        saveData = SaveData(this)
        if (saveData.loadDarkModeState() == true) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        toolbar = findViewById(R.id.toolbar)

        courseUid = intent.getStringExtra("courseUid")!!
        coursePartUid = intent.getStringExtra("coursePartUid")!!
        coursePartTitle = intent.getStringExtra("coursePartTitle")!!

        setSupportActionBar(toolbar)
        supportActionBar?.title = coursePartTitle
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setInfo()

        countQuestions()

        goToQuizButton.setOnClickListener {
            goToQuiz()
        }
    }

    private fun setInfo() {
        val ref = FirebaseDatabase.getInstance()
            .getReference("/course/$courseUid/parts/$coursePartUid")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val coursePart = p0.getValue(PartOfCourse::class.java)
                infoTextView.text = coursePart?.info ?: ""
                Picasso.get().load(coursePart?.imageUrl).into(infoImageView)
                }
            override fun onCancelled(p0: DatabaseError) { }
        })
    }

    private fun countQuestions() {
        val countQuestionRef = FirebaseDatabase.getInstance()
            .getReference("/course/$courseUid/parts/$coursePartUid/test/")

        countQuestionRef.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) { }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    p0.children.forEach {
                        countQuestions++
                    }
                }
            }
        })
    }

    private fun goToQuiz() {
        val intent = Intent(Intent(this, QuizActivity::class.java))
        intent.putExtra("courseUid", courseUid)
        intent.putExtra("coursePartUid", coursePartUid)
        intent.putExtra("coursePartTitle", coursePartTitle)
        intent.putExtra("countedQuestions", countQuestions)
        startActivity(intent)
    }
}
