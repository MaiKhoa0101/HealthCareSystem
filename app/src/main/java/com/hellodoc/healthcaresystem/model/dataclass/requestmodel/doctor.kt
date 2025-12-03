package com.hellodoc.healthcaresystem.requestmodel

import android.net.Uri
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.ServiceInput
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.ServiceOutput
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.WorkHour

data class ApplyDoctorRequest(
    val license: String,
    val specialty: String,
    val address: String,
    val CCCD: String,
    val licenseUrl: Uri?,
    val faceUrl: Uri?,
    val avatarURL: Uri?,
    val frontCccdUrl: Uri?,
    val backCccdUrl: Uri?
)

data class ModifyClinicRequest(
    val workingHours: List<WorkHour>,
    val oldWorkingHours: List<WorkHour>,
    val address: String,
    val description: String,
    val services: List<ServiceInput>,
    val oldServices: List<ServiceOutput>,
    val images: List<Uri>,
    val hasHomeService: Boolean,
    val isClinicPaused: Boolean
)

data class DoctorUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
