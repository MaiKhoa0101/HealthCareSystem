package com.parkingSystem.parkingSystem.roomDb.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.parkingSystem.parkingSystem.roomDb.data.dao.AppointmentDao
import com.parkingSystem.parkingSystem.roomDb.data.entity.AppointmentEntity


@Database(entities = [AppointmentEntity::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appointmentDao(): AppointmentDao
}