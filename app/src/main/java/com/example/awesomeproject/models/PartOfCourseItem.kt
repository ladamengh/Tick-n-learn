package com.example.awesomeproject.models

import com.example.awesomeproject.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.part_of_course_item_layout.view.*

class PartOfCourseItem(
    val partOfCourse: PartOfCourse
): Item<GroupieViewHolder>() {

    override fun getLayout(): Int {
        return R.layout.part_of_course_item_layout
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.coursePartTitleTextView.text = partOfCourse.title

        Picasso.get().load(partOfCourse.imageUrl).into(viewHolder.itemView.coursePartRecyclerViewImage)
    }
}