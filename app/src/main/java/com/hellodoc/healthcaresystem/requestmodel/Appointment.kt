package com.hellodoc.healthcaresystem.requestmodel

data class CreateAppointmentRequest(
    val doctorID: String,
    val patientID: String,
    val patientModel: String,
    val date: String, // Ví dụ: "2025-04-20"
    val time: String, // Ví dụ: "14:30"
//    val status: String, // optional
    val examinationMethod: String, // "in_person" hoặc "online"
    val notes: String,
    val reason: String,
    val totalCost: String,
    val location: String
)

data class UpdateAppointmentRequest(
    val time: String,
    val date: String,
    val notes: String
)
