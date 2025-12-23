package com.hellodoc.healthcaresystem.model.dataclass.responsemodel

import com.google.gson.annotations.SerializedName

data class AppointmentResponse(
    @SerializedName("_id") val id: String,
    val doctor: Doctor,
    val patientModel: String,
    val patient: Patient,
    val date: String,
    val time: String,
    val status: String,
    val examinationMethod: String,
    val notes: String,
    val location: String,
    val createdAt: String,
    val updatedAt: String,
    val note: String
    ){
    data class Doctor(
        @SerializedName("_id")
        val id: String,
        val name: String,
        val specialty: Specialty,
        val avatarURL: String
    )

    data class Patient(
        @SerializedName("_id")
        val id: String,
        val name: String,
        val avatarURL: String?
    )
    data class Specialty(
        @SerializedName("_id")
        val id: String,
        val name: String
    )
}

data class CreateAppointmentResponse(
    @SerializedName("message") val message: String,
    val appointment: Appointment
) {
    data class Appointment(
        val id: String,
        val doctor: String,
        val patient: String,
        val date: String,
        val time: String,
        val status: String,
        val examinationMethod: String,
        val reason: String,
        val notes: String?,
        val totalCost: Double
    )
}

data class CancelAppointmentResponse(
    val message: String
)

data class UpdateAppointmentResponse(
    val message: String
)