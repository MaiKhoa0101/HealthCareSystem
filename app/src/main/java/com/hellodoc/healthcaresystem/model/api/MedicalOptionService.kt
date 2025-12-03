package com.hellodoc.healthcaresystem.api

import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.GetMedicalOptionResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.GetRemoteMedicalOptionResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface MedicalOptionService {
    @Headers("Content-Type: application/json")
    @GET("medical-option/get-all")
    suspend fun getMedicalOptions(): Response<List<GetMedicalOptionResponse>>

    @GET("remote-medical-option/get-all")
    suspend fun getRemoteMedicalOptions(): Response<List<GetRemoteMedicalOptionResponse>>
}