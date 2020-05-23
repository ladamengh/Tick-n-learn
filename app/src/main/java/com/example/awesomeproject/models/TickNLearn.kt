package com.example.awesomeproject.models

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class TickNLearn: Application() {

    override fun onCreate() {
        super.onCreate()

        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}