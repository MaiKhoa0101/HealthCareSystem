package com.hellodoc.healthcaresystem.model.roomDb.data.database

import android.content.Context
import androidx.room.Room
import com.hellodoc.healthcaresystem.model.roomDb.data.dao.AppSettingsDao
import com.hellodoc.healthcaresystem.model.roomDb.data.dao.QuickResponseDao
import com.hellodoc.healthcaresystem.model.roomDb.data.dao.WordGraphDao // Đã thêm import này
import com.hellodoc.healthcaresystem.roomDb.data.dao.AppointmentDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "HelloDocDatabase"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideAppointmentDao(database: AppDatabase): AppointmentDao {
        return database.appointmentDao()
    }

    @Provides
    @Singleton
    fun provideQuickResponseDao(database: AppDatabase): QuickResponseDao {
        return database.quickResponseDao()
    }

    // --- BỔ SUNG PHẦN THIẾU QUAN TRỌNG ---
    @Provides
    @Singleton
    fun provideAppSettingsDao(database: AppDatabase): AppSettingsDao {
        return database.appSettingsDao()
    }

    // --- ĐÂY LÀ PHẦN BẠN ĐANG THIẾU ---
    @Provides
    @Singleton
    fun provideWordGraphDao(database: AppDatabase): WordGraphDao {
        return database.wordDao() // Hàm này phải khớp tên với hàm trong AppDatabase
    }
}