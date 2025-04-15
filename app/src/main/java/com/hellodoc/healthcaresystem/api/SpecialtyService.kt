package com.hellodoc.healthcaresystem.api

import com.hellodoc.healthcaresystem.responsemodel.GetDoctorBySpecialty
import com.hellodoc.healthcaresystem.responsemodel.GetSpecialtyResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface SpecialtyService {
    @Headers("Content-Type: application/json")
    @GET("specialty/get-all")
    suspend fun getSpecialties(): Response<List<GetSpecialtyResponse>>
    @GET("doctor/specialty/{id}")
    suspend fun getSpecialtyById(@Path("id") specialtyId: String): Response<GetSpecialtyResponse>
}