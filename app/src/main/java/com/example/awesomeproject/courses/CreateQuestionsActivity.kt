package com.example.awesomeproject.courses

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.awesomeproject.R
import com.example.awesomeproject.SaveData
import com.example.awesomeproject.models.Question
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_create_questions.*

class CreateQuestionsActivity : AppCompatActivity() {

    val auth = FirebaseAuth.getInstance()
    private lateinit var toolbar: Toolbar
    private var mQuestionNumber: Int = 1
    private lateinit var saveData: SaveData

    override fun onCreate(savedInstanceState: Bundle?) {

        saveData = SaveData(this)
        if (saveData.loadDarkModeState() == true) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_questions)

        toolbar = findViewById(R.id.toolbar)

        updateQuestionNumber(mQuestionNumber)

        val courseUid = intent.getStringExtra("courseUid")
        val courseTitle = intent.getStringExtra("courseTitle")
        val coursePartUid = intent.getStringExtra("coursePartUid")

        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(courseTitle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        createQuestionButton.setOnClickListener {
            createNewQuestion(courseUid!!, coursePartUid!!)
        }

        endCreatingQuestionButton.setOnClickListener {
            createNewQuestion(courseUid!!, coursePartUid!!)
            startActivity(Intent(this, CoursesListActivity::class.java))
        }
    }

    private fun createNewQuestion(courseUid: String, coursePartUid: String) {
        val ref = FirebaseDatabase.getInstance()
            .getReference("/course/$courseUid/parts/$coursePartUid/test/question$mQuestionNumber")

        val question = createQuestion.text.toString()
        val choice1 = createChoice1.text.toString()
        val choice2 = createChoice2.text.toString()
        val choice3 = createChoice3.text.toString()
        val choice4 = createChoice4.text.toString()
        val answer = createAnswer.text.toString()

        if (question.isEmpty() || choice1.isEmpty() || choice2.isEmpty()
            || choice3.isEmpty() || choice4.isEmpty() || answer.isEmpty())
        {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
        } else {
            val questionFull = Question(question, choice1, choice2, choice3, choice4, answer)

            ref.setValue(questionFull)
                .addOnSuccessListener {
                    Toast.makeText(this, "Вопрос добавлен", Toast.LENGTH_SHORT).show()
                    mQuestionNumber++
                    updateQuestionNumber(mQuestionNumber)
                    clearTexts()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun clearTexts() {
        createQuestion.text?.clear()
        createChoice1.text?.clear()
        createChoice2.text?.clear()
        createChoice3.text?.clear()
        createChoice4.text?.clear()
        createAnswer.text?.clear()
    }

    private fun updateQuestionNumber(mQuestionNumber: Int) {
        numberQBar.text = mQuestionNumber.toString()
    }
}

