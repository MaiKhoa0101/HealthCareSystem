package com.hellodoc.healthcaresystem.local


import androidx.room.Database
import androidx.room.RoomDatabase
import com.hellodoc.healthcaresystem.local.entity.AppointmentEntity
import com.hellodoc.healthcaresystem.local.dao.AppointmentDao

@Database(entities = [AppointmentEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appointmentDao(): AppointmentDao
}
