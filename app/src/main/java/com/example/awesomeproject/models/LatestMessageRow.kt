package com.example.awesomeproject.models

import com.example.awesomeproject.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessageRow(
    val chatMessage: ChatMessage
): Item<GroupieViewHolder>() {

    var chatPartnerUser: User? = null

    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.messageTextViewL.text = chatMessage.text

        val chatPartnerId: String

        if (chatMessage.fromID == FirebaseAuth.getInstance().uid) {
            chatPartnerId = chatMessage.toID
        } else {
            chatPartnerId = chatMessage.fromID
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) { }

            override fun onDataChange(p0: DataSnapshot) {
                chatPartnerUser = p0.getValue(User::class.java)
                viewHolder.itemView.usernameLatestTextView.text = chatPartnerUser?.username
                val targetImageView = viewHolder.itemView.userImageViewLatest
                Picasso.get().load(chatPartnerUser?.profileImageUrl).into(targetImageView)
            }

        })

    }
}