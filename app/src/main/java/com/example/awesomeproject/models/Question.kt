package com.example.awesomeproject.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Question(
    val question: String,
    val choice1: String,
    val choice2: String,
    val choice3: String,
    val choice4: String,
    val answer: String
): Parcelable {
    constructor(): this("", "", "", "", "", "")
}