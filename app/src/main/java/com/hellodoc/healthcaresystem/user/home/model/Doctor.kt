package com.hellodoc.healthcaresystem.user.home.model

import java.io.Serializable

// Dữ liệu bác sĩ
data class Doctor(
    val name: String,
    val specialty: String,
    val address: String
) : Serializable
