package com.example.awesomeproject.courses

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
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
import java.util.*

class CoursesListActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private var instance = FirebaseDatabase.getInstance()
    private var uid = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme()
        fetchCourses()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_courses_list)

        setTools()
        setNavigation()
        checkAdmin()

        searchText.doOnTextChanged { _, _, _, _ ->
            searchCourse(searchText.text.toString().trim())
        }

        createNewButton.setOnClickListener {
            startActivity(Intent(this, CreateNewCourseActivity::class.java))
        }
    }

    private fun setTools() {
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.coursesToolbarTitle)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        yourCoursesRecyclerView
            .addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }

    private fun setTheme() {
        val saveData = SaveData(this)
        if (saveData.loadDarkModeState() == true) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }

        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    private fun setNavigation() {
        bottomNavigation.selectedItemId = R.id.courses
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.profile -> {
                    startActivity(Intent(this, UserProfileActivity::class.java))
                }
                R.id.courses -> {
                    startActivity(Intent(this, CoursesListActivity::class.java))
                }
                R.id.dialogs -> {
                    startActivity(Intent(this, LatestMessagesActivity::class.java))
                }
            }
            true
        }
    }

    private fun fetchCourses() {

        val ref = instance.getReference("/course")
        ref.keepSynced(true)

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(courseSnapshot: DataSnapshot) {

                val adapter = GroupAdapter<GroupieViewHolder>()

                courseSnapshot.children.forEach {
                    val course = it.getValue(Course::class.java)
                    adapter.add(CourseItem(course!!))
                }

                adapter.setOnItemClickListener { item, view ->

                    val courseItem = item as CourseItem
                    val intent = Intent(view.context, CoursePartsActivity::class.java)
                    intent.putExtra("courseUid", courseItem.course.uid)
                    intent.putExtra("courseTitle", courseItem.course.title)
                    startActivity(intent)

                    finish()
                }

                yourCoursesRecyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                throw error.toException()
            }
        })
    }

    private fun searchCourse(searchText: String) {
        val adapter = GroupAdapter<GroupieViewHolder>()
        if (searchText.isEmpty()) {
            fetchCourses()
        } else {
            val ref = instance.getReference("/course")
            val searchQuery = ref
                .orderByChild("title")
                .startAt(searchText.toUpperCase(Locale.ROOT))
                .endAt("${searchText.toLowerCase(Locale.ROOT)}\uf8ff")

            searchQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(searchCourseSnapshot: DataSnapshot) {

                    searchCourseSnapshot.children.forEach {
                        val course = it.getValue(Course::class.java)
                        adapter.add(CourseItem(course!!))
                    }

                    adapter.setOnItemClickListener { item, view ->

                        val courseItem = item as CourseItem
                        val intent = Intent(view.context, CoursePartsActivity::class.java)
                        intent.putExtra("courseUid", courseItem.course.uid)
                        intent.putExtra("courseTitle", courseItem.course.title)
                        startActivity(intent)

                        finish()
                    }

                    yourCoursesRecyclerView.adapter = adapter
                }

                override fun onCancelled(error: DatabaseError) {
                    throw error.toException()
                }
            })
        }
    }

    private fun checkAdmin() {
        val rootRef = FirebaseDatabase.getInstance().reference
        val adminsRef = rootRef.child("admins")

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val value = ds.getValue(String::class.java)
                    if(value.equals(uid)) {
                        createNewButton.visibility = View.VISIBLE
                        break
                    } else {
                        createNewButton.isEnabled = false
                        createNewButton.visibility = View.INVISIBLE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                throw error.toException()
            }
        }
        adminsRef.addListenerForSingleValueEvent(valueEventListener)
    }
}
