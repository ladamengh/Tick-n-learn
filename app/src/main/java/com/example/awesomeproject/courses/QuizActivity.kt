package com.example.awesomeproject.courses

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.awesomeproject.R
import com.example.awesomeproject.SaveData
import com.example.awesomeproject.models.ScoreItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_quiz.*

class QuizActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var courseUid: String
    private lateinit var coursePartUid: String
    private lateinit var coursePartTitle: String
    private lateinit var currentUserUid: String
    private var countQuestions: Long = 0
    private var mScore: Int = 0
    private var mAnswer: String? = null
    private var mQuestionNumber: Int = 1
    private var instance = FirebaseDatabase.getInstance()
    private lateinit var saveData: SaveData

    override fun onCreate(savedInstanceState: Bundle?) {

        saveData = SaveData(this)
        if (saveData.loadDarkModeState() == true) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        toolbar = findViewById(R.id.toolbar)

        courseUid = intent.getStringExtra("courseUid")!!
        coursePartUid = intent.getStringExtra("coursePartUid")!!
        coursePartTitle = intent.getStringExtra("coursePartTitle")!!
        countQuestions = intent.getLongExtra("countedQuestions", 0)
        currentUserUid = FirebaseAuth.getInstance().currentUser?.uid.toString()

        setSupportActionBar(toolbar)
        supportActionBar?.title = coursePartTitle
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        updateQuestion()

        scoreBar.text = mScore.toString()

        buttonChoice1.setOnClickListener { buttonOnClick(buttonChoice1) }
        buttonChoice2.setOnClickListener { buttonOnClick(buttonChoice2) }
        buttonChoice3.setOnClickListener { buttonOnClick(buttonChoice3) }
        buttonChoice4.setOnClickListener { buttonOnClick(buttonChoice4) }
    }

    private fun buttonOnClick(buttonChoice: Button) {
        if (buttonChoice.text == mAnswer) {
            // сделать стиль кнопки
            //buttonChoice.background = getResources(getColor(R.color.colorGreen)))
            Toast.makeText(this, "Верно", Toast.LENGTH_SHORT).show()
            mScore++
            updateScore()

            if (mQuestionNumber <= countQuestions) {
                updateQuestion()
            } else {
                finishTest()
            }
        } else {
            Toast.makeText(this, "Неверно", Toast.LENGTH_SHORT).show()

            if (mQuestionNumber <= countQuestions) {
                updateQuestion()
            } else {
                finishTest()
            }
        }
    }

    private fun updateQuestion() {
        val mQuestionRef = instance
            .getReference("/course/$courseUid/parts/$coursePartUid/test/question$mQuestionNumber/question")

        mQuestionRef.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) { }

            override fun onDataChange(p0: DataSnapshot) {
                val question = p0.value.toString()
                questionQuiz.text = question
            }
        })

        val mChoice1Ref = instance
            .getReference("/course/$courseUid/parts/$coursePartUid/test/question$mQuestionNumber/choice1")

        mChoice1Ref.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) { }

            override fun onDataChange(p0: DataSnapshot) {
                val choice = p0.value.toString()
                buttonChoice1.text = choice
            }
        })

        val mChoice2Ref = instance
            .getReference("/course/$courseUid/parts/$coursePartUid/test/question$mQuestionNumber/choice2")

        mChoice2Ref.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) { }

            override fun onDataChange(p0: DataSnapshot) {
                val choice = p0.value.toString()
                buttonChoice2.text = choice
            }
        })

        val mChoice3Ref = instance
            .getReference("/course/$courseUid/parts/$coursePartUid/test/question$mQuestionNumber/choice3")

        mChoice3Ref.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) { }

            override fun onDataChange(p0: DataSnapshot) {
                val choice = p0.value.toString()
                buttonChoice3.text = choice
            }
        })

        val mChoice4Ref = instance
            .getReference("/course/$courseUid/parts/$coursePartUid/test/question$mQuestionNumber/choice4")

        mChoice4Ref.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) { }

            override fun onDataChange(p0: DataSnapshot) {
                val choice = p0.value.toString()
                buttonChoice4.text = choice
            }
        })

        val mAnswerRef = instance
            .getReference("/course/$courseUid/parts/$coursePartUid/test/question$mQuestionNumber/answer")

        mAnswerRef.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) { }

            override fun onDataChange(p0: DataSnapshot) {
                mAnswer = p0.value.toString()
            }
        })

        mQuestionNumber++
    }

    private fun saveScoreToDatabase() {
        val saveScoreRef =  instance
            .getReference("/course/$courseUid/parts/$coursePartUid/score/$currentUserUid")

        saveScoreRef.setValue(ScoreItem(currentUserUid, coursePartUid, mScore, coursePartTitle))
    }

    private fun finishTest() {
        saveScoreToDatabase()

        val intent = Intent(this, FinishTestActivity::class.java)
        intent.putExtra("courseUid", courseUid)
        intent.putExtra("coursePartUid", coursePartUid)
        intent.putExtra("coursePartTitle", coursePartTitle)
        intent.putExtra("score", mScore.toString())
        startActivity(intent)
        finish()
    }

    private fun updateScore() {
        scoreBar.text = mScore.toString()
    }
}
