package com.example.awesomeproject.courses

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.example.awesomeproject.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_courses_list.*

class CoursesListActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar

    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_courses_list)

        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.coursesToolbarTitle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        addNewCourseButton.setOnClickListener {
            startActivity(Intent(this, CreateNewCourseActivity::class.java))
        }

        goToQuizButton.setOnClickListener {
            startActivity(Intent(this, QuizActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_courses, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuNewCourse -> startActivity(Intent(this, NewCourseActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }
}
