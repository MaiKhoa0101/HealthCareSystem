package com.hellodoc.healthcaresystem.model.repository

import com.hellodoc.healthcaresystem.api.MedicalOptionService
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.GetMedicalOptionResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.GetRemoteMedicalOptionResponse
import jakarta.inject.Inject
import retrofit2.Response

interface MedicalOptionRepository {
    suspend fun getMedicalOptions(): Response<List<GetMedicalOptionResponse>>
    suspend fun getRemoteMedicalOptions(): Response<List<GetRemoteMedicalOptionResponse>>
}
class MedicalOptionRepositoryImpl @Inject constructor(
    private val medicalOptionService: MedicalOptionService
): MedicalOptionRepository {
    override suspend fun getMedicalOptions() = medicalOptionService.getMedicalOptions()
    override suspend fun getRemoteMedicalOptions() = medicalOptionService.getRemoteMedicalOptions()

}