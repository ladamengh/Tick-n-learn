package com.example.awesomeproject.courses

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.Global.getString
import android.widget.Button
import android.widget.Toast
import com.example.awesomeproject.R
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_quiz.*
import java.nio.channels.Channel

class QuizActivity : AppCompatActivity() {

    private var mScore: Int = 0
    private var mAnswer: String? = null
    private var mQuestionNumber: Int = 0
    private var mChoiceNumber: Int = 0
    private var instance = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        updateQuestion()

        scoreBar.text = mScore.toString()

        buttonChoice1.setOnClickListener { buttonOnClick(buttonChoice1) }
        buttonChoice2.setOnClickListener { buttonOnClick(buttonChoice2) }
        buttonChoice3.setOnClickListener { buttonOnClick(buttonChoice3) }
        buttonChoice4.setOnClickListener { buttonOnClick(buttonChoice4) }
    }

    private fun buttonOnClick(buttonChoice: Button) {
        if (buttonChoice.text.equals(mAnswer)) {
            // сделать стиль кнопки
            //buttonChoice.background = getResources(getColor(R.color.colorGreen)))
            Toast.makeText(this, "Верно", Toast.LENGTH_SHORT).show()
            mScore++
            updateScore(mScore)

            if (mQuestionNumber <= 10) {
                updateQuestion()
            } else {
                startActivity(Intent(this, FinishTestActivity::class.java))
            }
        } else {
            Toast.makeText(this, "Неверно", Toast.LENGTH_SHORT).show()

            if (mQuestionNumber <= 10) {
                updateQuestion()
            } else {
                startActivity(Intent(this, FinishTestActivity::class.java))
            }
        }
    }

    private fun updateQuestion() {
        val mQuestionRef = instance.getReference("/course/tests/" + mQuestionNumber + "/question")

        mQuestionRef.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) { }

            override fun onDataChange(p0: DataSnapshot) {
                val question = p0.getValue().toString()
                questionQuiz.text = question
            }
        })

        val mChoice1Ref = instance.getReference("/course/tests/" + mQuestionNumber + "/choice1")

        mChoice1Ref.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) { }

            override fun onDataChange(p0: DataSnapshot) {
                val choice = p0.getValue().toString()
                buttonChoice1.text = choice
            }
        })

        val mChoice2Ref = instance.getReference("/course/tests/" + mQuestionNumber + "/choice2")

        mChoice2Ref.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) { }

            override fun onDataChange(p0: DataSnapshot) {
                val choice = p0.getValue().toString()
                buttonChoice2.text = choice
            }
        })

        val mChoice3Ref = instance.getReference("/course/tests/" + mQuestionNumber + "/choice3")

        mChoice3Ref.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) { }

            override fun onDataChange(p0: DataSnapshot) {
                val choice = p0.getValue().toString()
                buttonChoice3.text = choice
            }
        })

        val mChoice4Ref = instance.getReference("/course/tests/" + mQuestionNumber + "/choice4")

        mChoice4Ref.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) { }

            override fun onDataChange(p0: DataSnapshot) {
                val choice = p0.getValue().toString()
                buttonChoice4.text = choice
            }
        })

        val mAnswerRef = instance.getReference("/course/tests/" + mQuestionNumber + "/answer")

        mAnswerRef.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) { }

            override fun onDataChange(p0: DataSnapshot) {
                mAnswer = p0.getValue().toString()
            }
        })

        mQuestionNumber++
    }

    private fun stopGame() {
        Toast.makeText(this, "Конец", Toast.LENGTH_SHORT).show()
    }

    private fun updateScore(score: Int) {
        scoreBar.text = score.toString()
    }
}
