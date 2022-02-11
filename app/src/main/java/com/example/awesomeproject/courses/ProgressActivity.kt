package com.example.awesomeproject.courses

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.awesomeproject.R
import com.example.awesomeproject.SaveData
import com.example.awesomeproject.models.PartOfCourse
import com.example.awesomeproject.models.ProgressLeft
import com.example.awesomeproject.models.ScoreItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.activity_progress.*
import kotlinx.android.synthetic.main.activity_progress.titleToolbar

class ProgressActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var courseUid: String
    private lateinit var courseTitle: String
    private lateinit var coursePartUid: String
    private lateinit var currentUserUid: String
    private lateinit var saveData: SaveData

    private val instance = FirebaseDatabase.getInstance()
    private val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progress)

        setBack()
        setTools()
        listenForProgress()

        goBackThemeP.setOnClickListener { goToTheme() }
    }

    private fun setTheme() {
        saveData = SaveData(this)
        if (saveData.loadDarkModeState() == true) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }
    }


    private fun setBack() {
        val saveData = SaveData(this)
        if (saveData.loadDarkModeState() == true) {
            layoutProgress.setBackgroundResource(R.drawable.black)
        } else {
            layoutProgress.setBackgroundResource(R.drawable.lamp_background)
        }
    }

    private fun setTools() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        titleToolbar.setText(R.string.progressToolbarTitle)
    }

    private fun listenForProgress() {
        courseUid = intent.getStringExtra("courseUid") ?: ""
        courseTitle = intent.getStringExtra("courseTitle") ?: "Темы"
        currentUserUid = FirebaseAuth.getInstance().currentUser?.uid.toString()

        val ref = FirebaseDatabase.getInstance().getReference("/course/$courseUid/parts/")

        progressRecyclerView.adapter = adapter

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                p0.children.forEach {
                    val coursePart = it.getValue(PartOfCourse::class.java)!!
                    coursePartUid = coursePart.uid

                    val saveScoreRef =  instance
                        .getReference("/course/$courseUid/parts/$coursePartUid/score/$currentUserUid")

                   saveScoreRef.addListenerForSingleValueEvent(object: ValueEventListener {
                       override fun onDataChange(p0: DataSnapshot) {
                           if (p0.exists()) {
                               val courseProgress = p0.getValue(ScoreItem::class.java)
                               adapter.add(ProgressLeft(courseProgress!!))
                           } else {
                               Toast.makeText(baseContext, R.string.noResults, Toast.LENGTH_LONG).show()
                                }
                           }

                       override fun onCancelled(error: DatabaseError) {
                           throw error.toException()
                       }
                   })
                }
                progressRecyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                throw error.toException()
            }
        })
    }

    private fun goToTheme() {
        val intent = Intent(Intent(this, CoursePartsActivity::class.java))
        intent.putExtra("courseUid", courseUid)
        intent.putExtra("courseTitle", courseTitle)
        startActivity(intent)
    }
}
