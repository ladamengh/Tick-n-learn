package com.example.awesomeproject.models

import com.example.awesomeproject.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.course_row_layout.view.*

class CourseItem(
    val course: Course
): Item<GroupieViewHolder>() {

    override fun getLayout(): Int {
        return R.layout.course_row_layout
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.courseTitleTextView.text = course.title
        viewHolder.itemView.courseDescriptionTextView.text = course.description

        Picasso.get().load(course.imageCourseUrl).into(viewHolder.itemView.courseRecyclerViewImage)
    }
}