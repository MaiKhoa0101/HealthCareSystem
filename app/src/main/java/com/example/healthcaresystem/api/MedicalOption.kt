package com.example.healthcaresystem.api

import com.example.healthcaresystem.responsemodel.GetMedicalOptionResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface MedicalOption {
    @Headers("Content-Type: application/json")
    @GET("medical-option/get-all")
    suspend fun getMedicalOptions(): Response<List<GetMedicalOptionResponse>>
}