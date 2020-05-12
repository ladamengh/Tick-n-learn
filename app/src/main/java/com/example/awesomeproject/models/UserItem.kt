package com.example.awesomeproject.models

import com.example.awesomeproject.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.user_row_new_message_layout.view.*

class UserItem(
    val user: User
): Item<GroupieViewHolder>() {

    override fun getLayout(): Int {
        return R.layout.user_row_new_message_layout
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.usernameTextViewRow.text = user.username

        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.userImageViewRow)
    }
}