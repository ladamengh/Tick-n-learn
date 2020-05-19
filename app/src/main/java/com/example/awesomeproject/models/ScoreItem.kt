package com.example.awesomeproject.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class ScoreItem(
    val userUid: String,
    val coursePartUid: String,
    val score: Int,
    val partTitle: String
): Parcelable {
    constructor(): this("", "", 0, "")
}