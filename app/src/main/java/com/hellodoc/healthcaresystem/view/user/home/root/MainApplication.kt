package com.hellodoc.healthcaresystem.view.user.home.root

import android.app.Application
import androidx.room.Room
import com.hellodoc.healthcaresystem.roomDb.data.database.AppDatabase
import dagger.hilt.android.HiltAndroidApp

class MainApplication : Application() {
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