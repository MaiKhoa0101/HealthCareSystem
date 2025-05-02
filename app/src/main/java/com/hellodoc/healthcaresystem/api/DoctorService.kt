package com.hellodoc.healthcaresystem.api

import com.hellodoc.healthcaresystem.responsemodel.GetDoctorResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface DoctorService {
    @Headers("Content-Type: application/json")
    @GET("doctor/get-all")
    suspend fun getDoctors(): Response<List<GetDoctorResponse>>

    @Headers("Content-Type: application/json")
    @GET("doctor/{id}")
    suspend fun getDoctorById(@Path("id") doctorId: String): Response<GetDoctorResponse>
}