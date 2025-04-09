package com.hellodoc.healthcaresystem.api

import com.hellodoc.healthcaresystem.responsemodel.GetDoctorResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface DoctorService {
    @Headers("Content-Type: application/json")
    @GET("doctor/get-all")
    suspend fun getDoctors(): Response<List<GetDoctorResponse>>
}