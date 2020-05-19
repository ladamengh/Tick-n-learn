package com.example.awesomeproject.models

import com.example.awesomeproject.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.progress_left_row_layout.view.*

class ProgressLeft(
    val progress: ScoreItem
): Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.progress_left_row_layout
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.scoreTextView.text = progress.score.toString()
        viewHolder.itemView.partTitleTextView.text = progress.partTitle
    }
}