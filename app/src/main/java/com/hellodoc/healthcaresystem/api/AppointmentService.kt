package com.hellodoc.healthcaresystem.api

import com.hellodoc.healthcaresystem.requestmodel.CreateAppointmentRequest
import com.hellodoc.healthcaresystem.responsemodel.AppointmentResponse
import com.hellodoc.healthcaresystem.responsemodel.CancelAppointmentResponse
import com.hellodoc.healthcaresystem.responsemodel.CreateAppointmentResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface AppointmentService {
    @Headers("Content-Type: application/json")
    @GET("appointments/getAll")
    suspend fun getAllAppointments(): Response<List<AppointmentResponse>>

    @GET("appointments/patient/{id}")
    suspend fun getAppointmentUser(@Path("id") id: String): Response<List<AppointmentResponse>>

    @GET("appointments/doctor/{id}")
    suspend fun getAppointmentDoctor(@Path("id") id: String): Response<List<AppointmentResponse>>

    @Headers("Content-Type: application/json")
    @POST("appointments/book")
    suspend fun createAppointment(
        @Header("accessToken") accessToken: String,
        @Body request: CreateAppointmentRequest
    ): Response<CreateAppointmentResponse>

    @PATCH("appointments/cancel/{id}")
    suspend fun cancelAppointment(@Path("id") id: String): Response<CancelAppointmentResponse>
}