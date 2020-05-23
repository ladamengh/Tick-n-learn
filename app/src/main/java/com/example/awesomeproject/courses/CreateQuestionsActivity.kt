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

    private val auth = FirebaseAuth.getInstance()
    private lateinit var toolbar: Toolbar
    private var questionNumber: Int = 1
    private lateinit var saveData: SaveData
    private lateinit var courseUid: String
    private lateinit var courseTitle: String
    private lateinit var coursePartUid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_questions)

        setTools()
        updateQuestionNumber(questionNumber)

        createQuestionButton.setOnClickListener {
            createNewQuestion()
        }

        endCreatingQuestionButton.setOnClickListener {
            createNewQuestion()
            startActivity(Intent(this, CoursesListActivity::class.java))
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
        courseTitle = intent.getStringExtra("courseTitle") ?: ""

        setSupportActionBar(toolbar)
        supportActionBar?.title = courseTitle
    }

    private fun createNewQuestion() {
        courseUid = intent.getStringExtra("courseUid") ?: ""
        coursePartUid = intent.getStringExtra("coursePartUid") ?: ""
        val ref = FirebaseDatabase.getInstance()
            .getReference("/course/$courseUid/parts/$coursePartUid/test/question$questionNumber")

        val question = createQuestion.text.toString()
        val choice1 = createChoice1.text.toString()
        val choice2 = createChoice2.text.toString()
        val choice3 = createChoice3.text.toString()
        val choice4 = createChoice4.text.toString()
        val answer = createAnswer.text.toString()

        if (question.isEmpty() || choice1.isEmpty() || choice2.isEmpty()
            || choice3.isEmpty() || choice4.isEmpty() || answer.isEmpty())
        {
            Toast.makeText(this, R.string.allFields, Toast.LENGTH_SHORT).show()
        } else {
            val questionFull = Question(question, choice1, choice2, choice3, choice4, answer)

            ref.setValue(questionFull)
                .addOnSuccessListener {
                    Toast.makeText(this, R.string.questionAdded, Toast.LENGTH_SHORT).show()
                    questionNumber++
                    updateQuestionNumber(questionNumber)
                    clearTexts()
                }
                .addOnFailureListener {
                    Toast.makeText(this, R.string.resetError, Toast.LENGTH_SHORT).show()
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

    private fun updateQuestionNumber(questionNumber: Int) {
        numberQBar.text = questionNumber.toString()
    }
}

