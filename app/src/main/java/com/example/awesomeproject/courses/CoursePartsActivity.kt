package com.example.awesomeproject.courses

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.awesomeproject.R
import com.example.awesomeproject.SaveData
import com.example.awesomeproject.models.PartOfCourse
import com.example.awesomeproject.models.PartOfCourseItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_course_part.*
import kotlinx.android.synthetic.main.activity_course_part.titleToolbar
import kotlinx.android.synthetic.main.activity_courses_list.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class CoursePartsActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var coursePartUid: String
    private lateinit var coursePartTitle: String
    private lateinit var courseUid: String
    private lateinit var courseTitle: String
    private var instance = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_part)

        courseTitle = intent.getStringExtra("courseTitle") ?: "Темы"

        setTools()
        fetchParts()

        goBackCourse.setOnClickListener {
            startActivity(Intent(this, CoursesListActivity::class.java))
        }
    }

    private fun setTools() {

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        titleToolbar.text = courseTitle
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        recyclerViewNewCoursePart.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )
    }

    private fun setTheme() {
        val saveData = SaveData(this)
        if (saveData.loadDarkModeState() == true) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }
    }

    private fun fetchParts() {
        courseUid = intent.getStringExtra("courseUid") ?: ""

        val ref = instance.getReference("/course/$courseUid/parts/")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(partSnapshot: DataSnapshot) {

                val adapter = GroupAdapter<GroupieViewHolder>()

                partSnapshot.children.forEach {
                    val coursePart = it.getValue(PartOfCourse::class.java)
                    adapter.add(PartOfCourseItem(coursePart!!))
                }

                adapter.setOnItemClickListener { item, view ->

                    val partOfCourseItem = item as PartOfCourseItem
                    val intent = Intent(view.context, InfoActivity::class.java)
                    coursePartUid = partOfCourseItem.partOfCourse.uid
                    coursePartTitle = partOfCourseItem.partOfCourse.title
                    intent.putExtra("courseUid", courseUid)
                    intent.putExtra("coursePartUid", coursePartUid)
                    intent.putExtra("coursePartTitle", coursePartTitle)
                    startActivity(intent)

                    finish()
                }

                recyclerViewNewCoursePart.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                throw error.toException()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_progress, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuProgress -> {
                val intent = Intent(this, ProgressActivity::class.java)
                intent.putExtra("courseUid", courseUid)
                intent.putExtra("courseTitle", courseTitle)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
