package com.example.awesomeproject.courses

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.awesomeproject.R
import com.example.awesomeproject.SaveData
import com.example.awesomeproject.models.PartOfCourse
import com.example.awesomeproject.models.PartOfCourseItem
import com.example.awesomeproject.models.ProgressLeft
import com.example.awesomeproject.models.ScoreItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_progress.*

class ProgressActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var courseUid: String
    private lateinit var coursePartUid: String
    private lateinit var coursePartTitle: String
    private lateinit var currentUserUid: String
    private lateinit var saveData: SaveData

    private val instance = FirebaseDatabase.getInstance()
    private val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {

        saveData = SaveData(this)
        if (saveData.loadDarkModeState() == true) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progress)

        toolbar = findViewById(R.id.toolbar)

        courseUid = intent.getStringExtra("courseUid")!!
        currentUserUid = FirebaseAuth.getInstance().currentUser?.uid.toString()

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Progress"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        progressRecyclerView.adapter = adapter

        listenForProgress()
    }

    private fun listenForProgress() {
        val ref = FirebaseDatabase.getInstance().getReference("/course/$courseUid/parts/")

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
                               Toast.makeText(baseContext, "Вы ещё не проходили тесты из данного курса", Toast.LENGTH_LONG).show()
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
}
