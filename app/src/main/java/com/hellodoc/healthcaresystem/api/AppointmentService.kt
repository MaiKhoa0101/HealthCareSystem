package com.example.healthcaresystem.api

import com.example.healthcaresystem.responsemodel.AppointmentResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface AppointmentService {
    @Headers("Content-Type: application/json")
    @GET("appointments/getAll")
    suspend fun getAllAppointments(): Response<List<AppointmentResponse>>
}