package com.example.awesomeproject.courses

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
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
    private var timeLeft: Long = 0L
    private var countQuestions: Int = 0
    private var score: Int = 0
    private var answer: String? = null
    private var questionNumber: Int = 1
    private var instance = FirebaseDatabase.getInstance()
    private var gameStarted: Boolean = false
    private lateinit var countDownTimer: CountDownTimer
    private var initialCountDown: Long = 30000
    private var countDownInterval: Long = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        updateQuestion()
        setTools()
        resetTimer()
        startTimer()

        buttonChoice1.setOnClickListener { buttonOnClick(buttonChoice1) }
        buttonChoice2.setOnClickListener { buttonOnClick(buttonChoice2) }
        buttonChoice3.setOnClickListener { buttonOnClick(buttonChoice3) }
        buttonChoice4.setOnClickListener { buttonOnClick(buttonChoice4) }
        buttonCancel.setOnClickListener { stopTest() }
    }

    private fun setTools() {
        scoreBar.text = score.toString()
        questionNum.text = countQuestions.toString()

        coursePartTitle = intent.getStringExtra("coursePartTitle") ?: "Title"

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.title = coursePartTitle
    }

    private fun setTheme() {
        val saveData = SaveData(this)
        if (saveData.loadDarkModeState() == true) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }
    }

    private fun resetTimer() {
        val initialTimeLeft = initialCountDown / 1000

        timeBar.text = initialTimeLeft.toString()
        countDownTimer = object: CountDownTimer(initialCountDown, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished / 1000
                timeBar.text = timeLeft.toString()
            }
            override fun onFinish() {
                Toast.makeText(baseContext, R.string.timeIsUp, Toast.LENGTH_LONG).show()
                updateQuestion()
            }
        }
        gameStarted = false
    }

    private fun buttonOnClick(buttonChoice: Button) {
        countDownTimer.cancel()
        if (buttonChoice.text == answer) {
            buttonChoice.setBackgroundResource(R.color.colorGreen)
            updateScore()

            if (questionNumber <= countQuestions) {
                updateQuestion()
                resetTimer()
                if (!gameStarted) {
                    startTimer()
                }
            } else {
                finishTest()
            }
        } else {
            buttonChoice.setBackgroundResource(R.color.colorRed)

            if (questionNumber <= countQuestions) {
                updateQuestion()
                resetTimer()
                if (!gameStarted) {
                    startTimer()
                }
            } else {
                finishTest()
            }
        }
    }

    private fun updateQuestion() {

        courseUid = intent.getStringExtra("courseUid") ?: ""
        coursePartUid = intent.getStringExtra("coursePartUid") ?: ""
        countQuestions = intent.getIntExtra("countedQuestions", 0)
        currentUserUid = FirebaseAuth.getInstance().currentUser?.uid.toString()

        val questionRef = instance
            .getReference("/course/$courseUid/parts/$coursePartUid/test/question$questionNumber/question")

        questionRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(qTitle: DataSnapshot) {
                val question = qTitle.value.toString()
                questionQuiz.text = question
            }
            override fun onCancelled(error: DatabaseError) {
                throw error.toException()
            }

        })

        val choice1Ref = instance
            .getReference("/course/$courseUid/parts/$coursePartUid/test/question$questionNumber/choice1")

        choice1Ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(choice1ds: DataSnapshot) {
                val choice = choice1ds.value.toString()
                buttonChoice1.setBackgroundResource(R.color.colorButton)
                buttonChoice1.text = choice
            }
            override fun onCancelled(error: DatabaseError) {
                throw error.toException()
            }
        })

        val choice2Ref = instance
            .getReference("/course/$courseUid/parts/$coursePartUid/test/question$questionNumber/choice2")

        choice2Ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(choice2ds: DataSnapshot) {
                val choice = choice2ds.value.toString()
                buttonChoice2.setBackgroundResource(R.color.colorButton)
                buttonChoice2.text = choice
            }
            override fun onCancelled(error: DatabaseError) {
                throw error.toException()
            }
        })

        val choice3Ref = instance
            .getReference("/course/$courseUid/parts/$coursePartUid/test/question$questionNumber/choice3")

        choice3Ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(choice3ds: DataSnapshot) {
                val choice = choice3ds.value.toString()
                buttonChoice3.setBackgroundResource(R.color.colorButton)
                buttonChoice3.text = choice
            }
            override fun onCancelled(error: DatabaseError) {
                throw error.toException()
            }
        })

        val choice4Ref = instance
            .getReference("/course/$courseUid/parts/$coursePartUid/test/question$questionNumber/choice4")

        choice4Ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(choice4ds: DataSnapshot) {
                val choice = choice4ds.value.toString()
                buttonChoice4.setBackgroundResource(R.color.colorButton)
                buttonChoice4.text = choice
            }
            override fun onCancelled(error: DatabaseError) {
                throw error.toException()
            }
        })

        val answerRef = instance
            .getReference("/course/$courseUid/parts/$coursePartUid/test/question$questionNumber/answer")

        answerRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(answerDs: DataSnapshot) {
                answer = answerDs.value.toString()
            }
            override fun onCancelled(error: DatabaseError) {
                throw error.toException()
            }
        })

        questionNumber++
    }

    private fun saveScoreToDatabase() {
        val saveScoreRef =  instance
            .getReference("/course/$courseUid/parts/$coursePartUid/score/$currentUserUid")

        saveScoreRef.setValue(ScoreItem(currentUserUid, coursePartUid, score, coursePartTitle))
    }

    private fun stopTest() {
        val intent = Intent(Intent(this, InfoActivity::class.java))
        intent.putExtra("courseUid", courseUid)
        intent.putExtra("coursePartUid", coursePartUid)
        intent.putExtra("coursePartTitle", coursePartTitle)
        startActivity(intent)
    }

    private fun finishTest() {
        saveScoreToDatabase()

        val intent = Intent(this, FinishTestActivity::class.java)
        intent.putExtra("courseUid", courseUid)
        intent.putExtra("coursePartUid", coursePartUid)
        intent.putExtra("coursePartTitle", coursePartTitle)
        intent.putExtra("score", score.toString())
        intent.putExtra("numQuestions", countQuestions.toString())
        startActivity(intent)
        finish()
    }

    private fun startTimer() {
        countDownTimer.start()
        gameStarted = true
    }

    private fun updateScore() {
        score++
        scoreBar.text = score.toString()
    }
}
