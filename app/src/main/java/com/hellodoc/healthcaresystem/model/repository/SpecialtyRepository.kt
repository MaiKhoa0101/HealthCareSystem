package com.hellodoc.healthcaresystem.model.repository

import com.hellodoc.healthcaresystem.model.api.SpecialtyService
import com.hellodoc.healthcaresystem.model.dataclass.requestmodel.AnalyzeSpecialtyRequest
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.GetSpecialtyResponse
import com.hellodoc.healthcaresystem.requestmodel.GetDoctorBySpecialtyNameRequest
import jakarta.inject.Inject
import okhttp3.MultipartBody
import retrofit2.Response

interface SpecialtyRepository {
    suspend fun getSpecialties(): Response<List<GetSpecialtyResponse>>
    suspend fun getSpecialtyById(specialtyId: String): Response<GetSpecialtyResponse>

    suspend fun createSpecialty(
        name: MultipartBody.Part,
        icon: MultipartBody.Part,
        description: MultipartBody.Part
    ): Response<GetSpecialtyResponse>
    suspend fun getSpecialtyByName(name: String): Response<GetSpecialtyResponse>
    suspend fun analyzeSpecialty(
        text: String,
        specialties: List<String>
    ):Response<Int>
}
class SpecialtyRepositoryImpl @Inject constructor(
    private val specialtyService: SpecialtyService
): SpecialtyRepository {
    override suspend fun getSpecialties() = specialtyService.getSpecialties()
    override suspend fun getSpecialtyById(specialtyId: String) = specialtyService.getSpecialtyById(specialtyId)
    override suspend fun createSpecialty(
        name: MultipartBody.Part,
        icon: MultipartBody.Part,
        description: MultipartBody.Part
    ) = specialtyService.createSpecialty(name, icon, description)
    override suspend fun getSpecialtyByName(name: String) = specialtyService.getSpecialtyByName(
        GetDoctorBySpecialtyNameRequest(name))

    override suspend fun analyzeSpecialty(text: String, specialties: List<String>) =
        specialtyService.analyzeSpecialty(AnalyzeSpecialtyRequest(text, specialties))

}