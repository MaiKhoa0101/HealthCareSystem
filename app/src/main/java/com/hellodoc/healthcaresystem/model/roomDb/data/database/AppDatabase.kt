package com.hellodoc.healthcaresystem.model.roomDb.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hellodoc.healthcaresystem.roomDb.data.dao.AppointmentDao
import com.hellodoc.healthcaresystem.roomDb.data.entity.AppointmentEntity


@Database(entities = [AppointmentEntity::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appointmentDao(): AppointmentDao
}