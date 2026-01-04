package com.hellodoc.healthcaresystem.model.roomDb.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hellodoc.healthcaresystem.model.roomDb.data.dao.QuickResponseDao
import com.hellodoc.healthcaresystem.model.roomDb.data.entity.QuickResponseEntity
import com.hellodoc.healthcaresystem.roomDb.data.dao.AppointmentDao
import com.hellodoc.healthcaresystem.roomDb.data.entity.AppointmentEntity


@Database(entities = [AppointmentEntity::class, QuickResponseEntity::class ], version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appointmentDao(): AppointmentDao
    abstract fun QuickResponseDao(): QuickResponseDao
}

