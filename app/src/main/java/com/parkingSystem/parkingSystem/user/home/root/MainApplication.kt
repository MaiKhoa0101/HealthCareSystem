package com.parkingSystem.parkingSystem.user.home.root

import android.app.Application
import androidx.room.Room
import com.parkingSystem.parkingSystem.roomDb.data.database.AppDatabase

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