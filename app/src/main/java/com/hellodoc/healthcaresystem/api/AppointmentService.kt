package com.hellodoc.healthcaresystem.api

import com.hellodoc.healthcaresystem.requestmodel.CreateAppointmentRequest
import com.hellodoc.healthcaresystem.responsemodel.AppointmentResponse
import com.hellodoc.healthcaresystem.responsemodel.CreateAppointmentResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface AppointmentService {
    @Headers("Content-Type: application/json")
    @GET("appointments/getAll")
    suspend fun getAllAppointments(): Response<List<AppointmentResponse>>

    @Headers("Content-Type: application/json")
    @POST("appointments/book")
    suspend fun createAppointment(@Body request: CreateAppointmentRequest): Response<CreateAppointmentResponse>
}