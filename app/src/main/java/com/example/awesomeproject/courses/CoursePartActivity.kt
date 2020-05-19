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
import com.example.awesomeproject.models.Course
import com.example.awesomeproject.models.CourseItem
import com.example.awesomeproject.models.PartOfCourse
import com.example.awesomeproject.models.PartOfCourseItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_course_part.*

class CoursePartActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var coursePartUid: String
    private lateinit var coursePartTitle: String
    private lateinit var courseUid: String
    private lateinit var courseTitle: String
    private lateinit var saveData: SaveData

    override fun onCreate(savedInstanceState: Bundle?) {

        saveData = SaveData(this)
        if (saveData.loadDarkModeState() == true) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_part)

        toolbar = findViewById(R.id.toolbar)

        recyclerViewNewCoursePart.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )

        courseUid = intent.getStringExtra("courseUid")!!
        courseTitle = intent.getStringExtra("courseTitle")!!

        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(courseTitle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fetchParts(courseUid)
    }

    private fun fetchParts(courseUid: String) {

        val ref = FirebaseDatabase.getInstance().getReference("/course/$courseUid/parts/")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                val adapter = GroupAdapter<GroupieViewHolder>()

                p0.children.forEach {
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

            override fun onCancelled(p0: DatabaseError) {

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
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
