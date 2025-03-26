package com.example.healthcaresystem.api

import com.example.healthcaresystem.model.GetUser
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface DoctorService {
    @Headers("Content-Type: application/json")
    @GET("doctor/get-all")
    suspend fun getUsers(): Response<List<GetUser>>
}