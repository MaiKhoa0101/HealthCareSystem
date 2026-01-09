package com.hellodoc.healthcaresystem.model.roomDb.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hellodoc.healthcaresystem.model.roomDb.data.dao.AppSettingsDao
import com.hellodoc.healthcaresystem.model.roomDb.data.dao.QuickResponseDao
import com.hellodoc.healthcaresystem.model.roomDb.data.dao.WordGraphDao
import com.hellodoc.healthcaresystem.model.roomDb.data.entity.AppSettingsEntity
import com.hellodoc.healthcaresystem.model.roomDb.data.entity.QuickResponseEntity
import com.hellodoc.healthcaresystem.model.roomDb.data.entity.WordEdgeEntity
import com.hellodoc.healthcaresystem.model.roomDb.data.entity.WordEntity
import com.hellodoc.healthcaresystem.roomDb.data.dao.AppointmentDao
import com.hellodoc.healthcaresystem.model.roomDb.data.entity.AppointmentEntity

@Database(
    exportSchema = false,
    entities = [
        AppointmentEntity::class,
        QuickResponseEntity::class,
        WordEntity::class,
        WordEdgeEntity::class,
        AppSettingsEntity::class // <--- Thêm dòng này
    ],
    version = 6 // <--- Tăng version lên 1 đơn vị
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appointmentDao(): AppointmentDao
    abstract fun quickResponseDao(): QuickResponseDao

    // Hàm này được gọi bởi DatabaseModule ở trên
    abstract fun wordDao(): WordGraphDao
    abstract fun appSettingsDao(): AppSettingsDao // <--- Thêm dòng này

}