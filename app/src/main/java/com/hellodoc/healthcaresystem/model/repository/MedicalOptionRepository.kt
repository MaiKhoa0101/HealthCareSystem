package com.hellodoc.healthcaresystem.model.repository

import com.hellodoc.healthcaresystem.api.MedicalOptionService
import jakarta.inject.Inject

class MedicalOptionRepository @Inject constructor(
    private val medicalOptionService: MedicalOptionService
){
    suspend fun getMedicalOptions() = medicalOptionService.getMedicalOptions()
}