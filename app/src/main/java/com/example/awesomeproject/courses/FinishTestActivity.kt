package com.example.awesomeproject.courses

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.example.awesomeproject.R
import com.example.awesomeproject.SaveData
import kotlinx.android.synthetic.main.activity_finish_test.*

class FinishTestActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var courseUid: String
    private lateinit var coursePartUid: String
    private lateinit var coursePartTitle: String
    private lateinit var score: String
    private lateinit var scoreComputer: String
    private lateinit var numQuestions: String
    private lateinit var saveData: SaveData

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finish_test)

        setTools()
        setWinner()
        goBackTextView.setOnClickListener { goBack() }
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

        coursePartTitle = intent.getStringExtra("coursePartTitle") ?: ""
        score = intent.getStringExtra("score") ?: ""
        scoreComputer = intent.getStringExtra("scoreComputer") ?: ""
        numQuestions = intent.getStringExtra("numQuestions") ?: ""
        courseUid = intent.getStringExtra("courseUid") ?: ""
        coursePartUid = intent.getStringExtra("coursePartUid") ?: ""

        setSupportActionBar(toolbar)
        supportActionBar?.title = coursePartTitle
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        scoreComputerText.text = scoreComputer
        scoreBar.text = score
        questionNum.text = numQuestions
    }

    private fun setWinner() {
        if (score > scoreComputer) {
            imageWinner.visibility = View.VISIBLE
        } else {
            imageLoser.visibility = View.VISIBLE
        }
    }

    private fun goBack() {
        val intent = Intent(Intent(this, InfoActivity::class.java))
        intent.putExtra("courseUid", courseUid)
        intent.putExtra("coursePartUid", coursePartUid)
        intent.putExtra("coursePartTitle", coursePartTitle)
        startActivity(intent)
    }
}
