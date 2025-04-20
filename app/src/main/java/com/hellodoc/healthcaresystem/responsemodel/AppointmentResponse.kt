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
        val email: String,
        val specialty: Specialty,
        val hospital: String
    )

    data class Patient(
        @SerializedName("_id") val id: String,
        val name: String,
        val email: String
    )

    data class Specialty(
        val name: String
    )
}
