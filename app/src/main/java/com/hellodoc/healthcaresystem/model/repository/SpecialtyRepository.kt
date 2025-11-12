package com.hellodoc.healthcaresystem.model.repository

import com.hellodoc.healthcaresystem.api.SpecialtyService
import jakarta.inject.Inject
import okhttp3.MultipartBody

class SpecialtyRepository @Inject constructor(
    private val specialtyService: SpecialtyService
){
    suspend fun getSpecialties() = specialtyService.getSpecialties()
    suspend fun getSpecialtyById(specialtyId: String) = specialtyService.getSpecialtyById(specialtyId)
    suspend fun createSpecialty(
        name: MultipartBody.Part,
        icon: MultipartBody.Part,
        description: MultipartBody.Part
    ) = specialtyService.createSpecialty(name, icon, description)

}