package com.hellodoc.healthcaresystem.appointment.model


data class AppointmentRow(
    val id: String,
    val patientName: String,
    val diagnosis: String,
    val doctor: String,
    val time: String,
    val location: String,
    val createdAt: String,
    val status: String
)
