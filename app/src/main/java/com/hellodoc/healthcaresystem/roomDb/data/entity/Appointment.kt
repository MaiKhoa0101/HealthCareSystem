package com.hellodoc.healthcaresystem.roomDb.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "appointments")
data class AppointmentEntity(
    @PrimaryKey
    val id: String,
    val patientId: String,
    val doctorId: String,
    val specialtyId: String,
    val appointmentDate: String,
    val appointmentTime: String,
    val status: String,
    val reason: String?,
    val notes: String?,
    val createdAt: String,
    val updatedAt: String,

    // Các field có thể null
    val doctorName: String?,
    val doctorAvatarUrl: String?,
    val patientName: String?,
    val specialtyName: String?
)