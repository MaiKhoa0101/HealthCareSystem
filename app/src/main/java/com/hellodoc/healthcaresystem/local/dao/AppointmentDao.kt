package com.hellodoc.healthcaresystem.local.dao

import androidx.room.*
import com.hellodoc.healthcaresystem.local.entity.AppointmentEntity

@Dao
interface AppointmentDao {
    @Query("SELECT * FROM appointments")
    suspend fun getAllAppointments(): List<AppointmentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointments(appointments: List<AppointmentEntity>)

    @Query("DELETE FROM appointments")
    suspend fun clearAppointments()
}
