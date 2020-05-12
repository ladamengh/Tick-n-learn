package com.example.awesomeproject.models

import com.example.awesomeproject.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_to_row_layout.view.*

class ChatToItem(
    val text: String,
    val user: User
): Item<GroupieViewHolder>() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.messageToUserTextView.text = text

        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.userToImageView
        Picasso.get().load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row_layout
    }
}