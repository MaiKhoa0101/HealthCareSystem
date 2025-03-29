package com.example.healthcaresystem.api

import com.example.healthcaresystem.responsemodel.GetSpecialtyResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface Specialty {
    @Headers("Content-Type: application/json")
    @GET("specialty/get-all")
    suspend fun getSpecialtys(): Response<List<GetSpecialtyResponse>>
}