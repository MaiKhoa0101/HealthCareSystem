package com.hellodoc.healthcaresystem.user.home

import android.app.Application
import androidx.room.Room
import com.hellodoc.healthcaresystem.local.AppDatabase

class MyApplication : Application() {
    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "hellodoc-db"
        ).fallbackToDestructiveMigration()
            .build()

    }
}
