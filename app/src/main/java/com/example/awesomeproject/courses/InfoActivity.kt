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
    private lateinit var courseTitle: String
    private lateinit var coursePartUid: String
    private lateinit var coursePartTitle: String
    private var countQuestions: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        setBack()
        setTools()
        setInfo()
        countQuestions()

        goToQuizButton.setOnClickListener { goToQuiz() }
        goBackTheme.setOnClickListener { goToTheme() }
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
        coursePartTitle = intent.getStringExtra("coursePartTitle") ?: ""

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        titleToolbar.text = coursePartTitle
    }

    private fun setBack() {
        val saveData = SaveData(this)
        if (saveData.loadDarkModeState() == true) {
            layoutInfo.setBackgroundResource(R.drawable.black)
        } else {
            layoutInfo.setBackgroundResource(R.drawable.courses_background)
        }
    }

    private fun setInfo() {
        courseUid = intent.getStringExtra("courseUid") ?: ""
        courseTitle = intent.getStringExtra("courseTitle") ?: "Темы"
        coursePartUid = intent.getStringExtra("coursePartUid") ?: ""

        val ref = FirebaseDatabase.getInstance()
            .getReference("/course/$courseUid/parts/$coursePartUid")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(partInfoSnapshot: DataSnapshot) {
                val coursePart = partInfoSnapshot.getValue(PartOfCourse::class.java)
                infoTextView.text = coursePart?.info ?: ""
                Picasso.get().load(coursePart?.imageUrl).into(infoImageView)
                }
            override fun onCancelled(error: DatabaseError) {
                throw error.toException()
            }
        })
    }

    private fun countQuestions() {
        val countQuestionRef = FirebaseDatabase.getInstance()
            .getReference("/course/$courseUid/parts/$coursePartUid/test/")

        countQuestionRef.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                throw error.toException()
            }

            override fun onDataChange(questionsSnapshot: DataSnapshot) {
                if (questionsSnapshot.exists()) {
                    questionsSnapshot.children.forEach { _ ->
                        countQuestions++
                    }
                }
                if (countQuestions > 0) {
                    goToQuizButton.isEnabled = true
                } else {
                    goToQuizButton.isEnabled = false
                    goToQuizButton.setText(R.string.noTest)
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

    private fun goToTheme() {
        val intent = Intent(Intent(this, CoursePartsActivity::class.java))
        intent.putExtra("courseUid", courseUid)
        intent.putExtra("courseTitle", courseTitle)
        startActivity(intent)
    }
}
