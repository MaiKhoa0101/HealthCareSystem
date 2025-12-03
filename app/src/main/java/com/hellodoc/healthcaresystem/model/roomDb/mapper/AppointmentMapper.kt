package com.hellodoc.healthcaresystem.roomDb.mapper

import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.AppointmentResponse
import com.hellodoc.healthcaresystem.roomDb.data.entity.AppointmentEntity

// Response -> Entity (Fixed với null safety)
fun AppointmentResponse.toEntity(): AppointmentEntity {
    return AppointmentEntity(
        id = this.id ?: "",
        patientId = this.patient?.id ?: "", // Thêm null check
        doctorId = this.doctor?.id ?: "", // Thêm null check
        specialtyId = this.doctor?.specialty?.id ?: "", // Thêm null check
        appointmentDate = this.date ?: "",
        appointmentTime = this.time ?: "",
        status = this.status ?: "SCHEDULED",
        reason = this.note,
        notes = this.notes,
        location = this.location,
        createdAt = this.createdAt ?: System.currentTimeMillis().toString(),
        updatedAt = this.updatedAt ?: System.currentTimeMillis().toString(),
        doctorName = this.doctor?.name,
        doctorAvatarUrl = this.doctor?.avatarURL,
        patientName = this.patient?.name,
        specialtyName = this.doctor?.specialty?.name
    )
}

// Phiên bản an toàn hơn với try-catch
fun AppointmentResponse.toEntitySafe(): AppointmentEntity {
    return try {
        AppointmentEntity(
            id = this.id ?: "",
            patientId = this.patient?.id ?: "",
            doctorId = this.doctor?.id ?: "",
            specialtyId = this.doctor?.specialty?.id ?: "",
            appointmentDate = this.date ?: "",
            appointmentTime = this.time ?: "",
            status = this.status ?: "SCHEDULED",
            reason = this.note,
            notes = this.notes,
            location = this.location,
            createdAt = this.createdAt ?: System.currentTimeMillis().toString(),
            updatedAt = this.updatedAt ?: System.currentTimeMillis().toString(),
            doctorName = this.doctor?.name,
            doctorAvatarUrl = this.doctor?.avatarURL,
            patientName = this.patient?.name,
            specialtyName = this.doctor?.specialty?.name
        )
    } catch (e: Exception) {
        println("Lỗi convert appointment ${this.id}: ${e.message}")
        e.printStackTrace()

        // Trả về entity với dữ liệu tối thiểu
        AppointmentEntity(
            id = this.id ?: "ERROR_${System.currentTimeMillis()}",
            patientId = "",
            doctorId = "",
            specialtyId = "",
            appointmentDate = this.date ?: "",
            appointmentTime = this.time ?: "",
            status = "ERROR",
            reason = "Conversion failed: ${e.message}",
            notes = this.notes,
            location = this.location,
            createdAt = System.currentTimeMillis().toString(),
            updatedAt = System.currentTimeMillis().toString(),
            doctorName = null,
            doctorAvatarUrl = null,
            patientName = null,
            specialtyName = null
        )
    }
}

// Entity -> Response (đã có, giữ nguyên)
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
        patientModel = "",
        patient = AppointmentResponse.Patient(
            id = this.patientId,
            name = this.patientName ?: ""
        ),
        date = this.appointmentDate,
        time = this.appointmentTime,
        status = this.status,
        examinationMethod = "",
        notes = this.notes ?: "",
        location = this.location,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        note = this.reason ?: ""
    )
}

// Extension function để validate AppointmentResponse trước khi convert
fun AppointmentResponse.isValid(): Boolean {
    return try {
        !this.id.isNullOrEmpty() &&
                this.patient != null &&
                !this.patient.id.isNullOrEmpty() &&
                this.doctor != null &&
                !this.doctor.id.isNullOrEmpty()
    } catch (e: Exception) {
        false
    }
}

