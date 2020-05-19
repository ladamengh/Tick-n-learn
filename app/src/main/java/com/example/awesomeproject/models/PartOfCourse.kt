package com.example.awesomeproject.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class PartOfCourse(
    val uid: String,
    val imageUrl: String,
    val title: String,
    val info: String
): Parcelable {
    constructor(): this("", "", "", "")
}