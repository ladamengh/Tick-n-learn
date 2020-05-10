package com.example.awesomeproject.models

import com.example.awesomeproject.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_from_row_layout.view.*

class ChatFromItem(val text: String, val user: User): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.messageFromUserTextView.text = text

        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.userFromImageView
        Picasso.get().load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row_layout
    }
}