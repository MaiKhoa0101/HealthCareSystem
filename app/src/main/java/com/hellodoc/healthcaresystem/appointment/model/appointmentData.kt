package com.hellodoc.healthcaresystem.appointment.model


data class AppointmentRow(
    val id: String,
    val doctorID: String,
    val doctorName: String,
    val patientID: String,
    val patientName: String,
    val specialty: String,
    val note: String,
    val time: String,
    val day: String,
    val location: String,
    val createdAt: String,
    val status: String
)
