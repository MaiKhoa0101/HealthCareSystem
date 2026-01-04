package com.hellodoc.healthcaresystem.model.roomDb.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hellodoc.healthcaresystem.model.roomDb.data.dao.QuickResponseDao
import com.hellodoc.healthcaresystem.model.roomDb.data.dao.WordGraphDao
import com.hellodoc.healthcaresystem.model.roomDb.data.entity.QuickResponseEntity
import com.hellodoc.healthcaresystem.model.roomDb.data.entity.WordEdgeEntity
import com.hellodoc.healthcaresystem.model.roomDb.data.entity.WordEntity
import com.hellodoc.healthcaresystem.roomDb.data.dao.AppointmentDao
import com.hellodoc.healthcaresystem.roomDb.data.entity.AppointmentEntity


@Database(entities = [AppointmentEntity::class, QuickResponseEntity::class, WordEntity::class, WordEdgeEntity::class ], version = 5)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appointmentDao(): AppointmentDao
    abstract fun quickResponseDao(): QuickResponseDao

    // Hàm này được gọi bởi DatabaseModule ở trên
    abstract fun wordDao(): WordGraphDao
}
