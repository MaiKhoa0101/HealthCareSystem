package com.hellodoc.healthcaresystem.api

import com.hellodoc.healthcaresystem.responsemodel.GetSpecialtyResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface SpecialtyService {
    @Headers("Content-Type: application/json")
    @GET("specialty/get-all")
    suspend fun getSpecialties(): Response<List<GetSpecialtyResponse>>
}