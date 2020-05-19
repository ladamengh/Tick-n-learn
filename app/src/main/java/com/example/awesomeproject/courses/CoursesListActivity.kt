package com.example.awesomeproject.courses

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.awesomeproject.R
import com.example.awesomeproject.SaveData
import com.example.awesomeproject.UserProfileActivity
import com.example.awesomeproject.messages.LatestMessagesActivity
import com.example.awesomeproject.models.Course
import com.example.awesomeproject.models.CourseItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_courses_list.*
import kotlinx.android.synthetic.main.activity_courses_list.bottomNavigation

class CoursesListActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var saveData: SaveData
    private var instance = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {

        saveData = SaveData(this)
        if (saveData.loadDarkModeState() == true) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_courses_list)

        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.coursesToolbarTitle)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        yourCoursesRecyclerView.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )

        bottomNavigation.selectedItemId = R.id.courses
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.profile -> {
                    startActivity(Intent(this, UserProfileActivity::class.java))
                }
                R.id.courses -> {
                    true
                }
                R.id.dialogs -> {
                    startActivity(Intent(this, LatestMessagesActivity::class.java))
                }
            }
            true
        }

        fetchCourses()

        searchText.doOnTextChanged { text, start, count, after ->

            val searchText = searchText.text.toString().trim()

            searchCourse(searchText)
        }
    }

    private fun fetchCourses() {

        val ref = instance.getReference("/course")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                val adapter = GroupAdapter<GroupieViewHolder>()

                p0.children.forEach {
                    val course = it.getValue(Course::class.java)
                    adapter.add(CourseItem(course!!))
                }

                adapter.setOnItemClickListener { item, view ->

                    val courseItem = item as CourseItem
                    val intent = Intent(view.context, CoursePartActivity::class.java)
                    intent.putExtra("courseUid", courseItem.course.uid)
                    intent.putExtra("courseTitle", courseItem.course.title)
                    startActivity(intent)

                    finish()
                }

                yourCoursesRecyclerView.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) { }
        })
    }

    private fun searchCourse(searchText: String) {
        val adapter = GroupAdapter<GroupieViewHolder>()
        if (searchText.isEmpty()) {
            fetchCourses()
        } else {
            val ref = instance.getReference("/course")
            val searchQuery = ref.orderByChild("title").startAt(searchText).endAt("$searchText\uf8ff")

            searchQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {

                    p0.children.forEach {
                        val course = it.getValue(Course::class.java)
                        adapter.add(CourseItem(course!!))
                    }

                    adapter.setOnItemClickListener { item, view ->

                        val courseItem = item as CourseItem
                        val intent = Intent(view.context, CoursePartActivity::class.java)
                        intent.putExtra("courseUid", courseItem.course.uid)
                        intent.putExtra("courseTitle", courseItem.course.title)
                        startActivity(intent)

                        finish()
                    }

                    yourCoursesRecyclerView.adapter = adapter
                }

                override fun onCancelled(p0: DatabaseError) {}
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_courses, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuNewCourse -> startActivity(Intent(this, CreateNewCourseActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }
}
