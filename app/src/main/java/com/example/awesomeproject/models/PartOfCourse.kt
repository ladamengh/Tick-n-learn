package com.example.awesomeproject.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.sql.Timestamp

@Parcelize
class PartOfCourse(
    val uid: String,
    val imageUrl: String,
    val title: String,
    val info: String,
    val timeStamp: Long
): Parcelable {
    constructor(): this("", "", "", "", -1)
}