package com.parkingSystem.parkingSystem.roomDb.data.dao

import androidx.room.*
import com.parkingSystem.parkingSystem.roomDb.data.entity.AppointmentEntity

@Dao
interface AppointmentDao {
    @Query("SELECT * FROM appointments")
    suspend fun getAllAppointments(): List<AppointmentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointments(appointments: List<AppointmentEntity>)

    @Query("DELETE FROM appointments")
    suspend fun clearAppointments()

    @Query("DELETE FROM appointments WHERE patientId = :patientId")
    suspend fun clearPatientAppointments(patientId: String)

    @Query("DELETE FROM appointments WHERE doctorId = :doctorId")
    suspend fun clearDoctorAppointments(doctorId: String)

    //lịch hẹn patient
    @Query("SELECT * FROM appointments WHERE patientId = :patientId ORDER BY appointmentDate DESC, appointmentTime DESC")
    suspend fun getPatientAppointments(patientId: String): List<AppointmentEntity>

    // Lấy lịch hẹn của bác sĩ theo doctorId
    @Query("SELECT * FROM appointments WHERE doctorId = :doctorId ORDER BY appointmentDate DESC, appointmentTime DESC")
    suspend fun getDoctorAppointments(doctorId: String): List<AppointmentEntity>
}