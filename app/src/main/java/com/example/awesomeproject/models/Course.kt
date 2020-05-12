package com.example.awesomeproject.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Course(
    val uid: String,
    val imageCourseUrl: String,
    val title: String,
    val description: String
): Parcelable {
    constructor(): this("", "", "", "")
}