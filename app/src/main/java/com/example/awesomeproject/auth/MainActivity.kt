package com.example.awesomeproject.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.awesomeproject.R
import com.example.awesomeproject.SaveData
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        regButton.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        logButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun setTheme() {
        val saveData = SaveData(this)
        if (saveData.loadDarkModeState() == true) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }
    }
}
