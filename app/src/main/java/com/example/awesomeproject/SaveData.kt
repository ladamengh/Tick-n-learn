package com.example.awesomeproject

import android.content.Context
import android.content.SharedPreferences

class SaveData(context: Context) {
    private var sharedPreferences: SharedPreferences = context.getSharedPreferences("file", Context.MODE_PRIVATE)

    // save the night mode state: true or false
    fun setDarkModeState(state: Boolean?) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("Dark", state!!)
        editor.apply()
    }

    fun loadDarkModeState(): Boolean? {
        val state = sharedPreferences.getBoolean("Dark", false)
        return (state)
    }
}