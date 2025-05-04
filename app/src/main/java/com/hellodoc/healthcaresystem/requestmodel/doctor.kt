package com.hellodoc.healthcaresystem.requestmodel

import android.net.Uri
import com.hellodoc.healthcaresystem.responsemodel.ServiceInput
import com.hellodoc.healthcaresystem.responsemodel.ServiceOutput
import com.hellodoc.healthcaresystem.responsemodel.WorkHour

data class ApplyDoctorRequest(
    val license: String,
    val specialty: String,
    val CCCD: String,
    val licenseUrl: Uri?,
    val faceUrl: Uri?,
    val avatarURL: Uri?,
    val frontCccdUrl: Uri?,
    val backCccdUrl: Uri?
)

data class ModifyClinic(
    val workingHours:List<WorkHour>,
    val address: String,
    val services: List<ServiceInput>
)


data class DoctorUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
