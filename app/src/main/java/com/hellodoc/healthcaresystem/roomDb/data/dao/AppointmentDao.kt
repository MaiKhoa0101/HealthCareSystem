package com.hellodoc.healthcaresystem.roomDb.data.dao

import androidx.room.*
import com.hellodoc.healthcaresystem.roomDb.data.entity.AppointmentEntity

@Dao
interface AppointmentDao {
    @Query("SELECT * FROM appointments")
    suspend fun getAllAppointments(): List<AppointmentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointments(appointments: List<AppointmentEntity>)

    @Query("DELETE FROM appointments")
    suspend fun clearAppointments()
}