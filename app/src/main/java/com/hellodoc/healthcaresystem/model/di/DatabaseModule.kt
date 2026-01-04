package com.hellodoc.healthcaresystem.model.di

import android.content.Context
import androidx.room.Room
import com.hellodoc.healthcaresystem.model.roomDb.data.dao.QuickResponseDao
import com.hellodoc.healthcaresystem.roomDb.data.dao.AppointmentDao
import com.hellodoc.healthcaresystem.model.roomDb.data.database.AppDatabase
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
            "your_database_name.db"
        )
            .fallbackToDestructiveMigration() // ✅ THÊM DÒNG NÀY
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
        return database.QuickResponseDao()
    }
}