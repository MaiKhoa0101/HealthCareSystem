package com.hellodoc.healthcaresystem.responsemodel

import com.google.gson.annotations.SerializedName

data class AppointmentResponse(
    @SerializedName("_id") val id: String,
    val doctor: Doctor,
    val patient: Patient,
    val date: String,
    val time: String,
    val status: String,
    val reason: String,
    val notes: String?,

    ) {
    data class Doctor(
        @SerializedName("_id") val id: String,
        val name: String,
        val email: String
    )

    data class Patient(
        @SerializedName("_id") val id: String,
        val name: String,
        val email: String
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
