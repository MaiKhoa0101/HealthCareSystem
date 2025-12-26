package com.hellodoc.healthcaresystem.model.repository

import com.hellodoc.healthcaresystem.model.api.SpecialtyService
import com.hellodoc.healthcaresystem.requestmodel.GetDoctorBySpecialtyNameRequest
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
    suspend fun getSpecialtyByName(name: String) = specialtyService.getSpecialtyByName(
        GetDoctorBySpecialtyNameRequest(name))

}