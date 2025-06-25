package com.hellodoc.healthcaresystem.roomDb.mapper

import com.hellodoc.healthcaresystem.responsemodel.AppointmentResponse
import com.hellodoc.healthcaresystem.roomDb.data.entity.AppointmentEntity

// Response -> Entity
fun AppointmentResponse.toEntity(): AppointmentEntity {
    return AppointmentEntity(
        id = this.id,
        patientId = this.patient.id,
        doctorId = this.doctor.id,
        specialtyId = this.doctor.specialty.id,
        appointmentDate = this.date,
        appointmentTime = this.time,
        status = this.status,
        reason = this.note, // ánh xạ "note" trong response -> "reason" trong entity
        notes = this.notes,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        doctorName = this.doctor.name,
        doctorAvatarUrl = this.doctor.avatarURL,
        patientName = this.patient.name,
        specialtyName = this.doctor.specialty.name
    )
}

// Entity -> Response
fun AppointmentEntity.toResponse(): AppointmentResponse {
    return AppointmentResponse(
        id = this.id,
        doctor = AppointmentResponse.Doctor(
            id = this.doctorId,
            name = this.doctorName ?: "",
            specialty = AppointmentResponse.Specialty(
                id = this.specialtyId,
                name = this.specialtyName ?: ""
            ),
            avatarURL = this.doctorAvatarUrl ?: ""
        ),
        patientModel = "", // Không có trong entity
        patient = AppointmentResponse.Patient(
            id = this.patientId,
            name = this.patientName ?: ""
        ),
        date = this.appointmentDate,
        time = this.appointmentTime,
        status = this.status,
        examinationMethod = "", // Không có trong entity
        notes = this.notes ?: "",
        location = "", // Không có trong entity
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        note = this.reason ?: ""
    )
}