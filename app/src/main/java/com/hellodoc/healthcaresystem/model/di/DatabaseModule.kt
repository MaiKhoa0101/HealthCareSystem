package com.hellodoc.healthcaresystem.model.di


import android.content.Context
import androidx.room.Room
import com.hellodoc.healthcaresystem.roomDb.data.dao.AppointmentDao
import com.hellodoc.healthcaresystem.model.roomDb.data.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Cung cấp trên toàn ứng dụng
object DatabaseModule {

    /**
     * Dạy Hilt cách cung cấp Room Database.
     * Nó sẽ là một Singleton để cả ứng dụng dùng chung 1 database.
     */
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase { // <-- (1)
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java, // <-- (1) THAY TÊN NÀY BẰNG TÊN DATABASE CỦA BẠN
            "your_database_name.db"  // <-- THAY BẰNG TÊN DATABASE CỦA BẠN
        ).build()
    }

    /**
     * Dạy Hilt cách cung cấp AppointmentDao.
     * Hilt sẽ tự động biết cách cung cấp 'database: AppDatabase' nhờ hàm ở trên.
     */
    @Provides
    @Singleton // Thường thì DAO cũng nên là Singleton
    fun provideAppointmentDao(database: AppDatabase): AppointmentDao { // <-- (1)
        return database.appointmentDao() // (Hàm này trong file AppDatabase của bạn)
    }
}