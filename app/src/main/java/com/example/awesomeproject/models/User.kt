package com.example.awesomeproject.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

// this annotation helps to implement parcelable methods to pass object through intents
@Parcelize
class User(val uid: String, val username: String, val profileImageUrl: String): Parcelable {
    constructor(): this("", "", "")

}