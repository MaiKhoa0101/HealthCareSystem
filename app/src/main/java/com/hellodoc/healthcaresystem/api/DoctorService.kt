package com.hellodoc.healthcaresystem.api

import com.hellodoc.healthcaresystem.responsemodel.ApplyDoctor
import com.hellodoc.healthcaresystem.responsemodel.GetDoctorResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface DoctorService {
    @Headers("Content-Type: application/json")
    @GET("doctor/get-all")
    suspend fun getDoctors(): Response<List<GetDoctorResponse>>

    @Headers("Content-Type: application/json")
    @GET("doctor/{id}")
    suspend fun getDoctorById(@Path("id") doctorId: String): Response<GetDoctorResponse>

    @Multipart
    @PATCH("doctor/apply-for-doctor/{id}")
    suspend fun applyForDoctor(
        @Path("id") id: String,
        @Part license: MultipartBody.Part,
        @Part specialty: MultipartBody.Part,
        @Part CCCD: MultipartBody.Part,
        @Part licenseUrl: MultipartBody.Part?,
        @Part faceUrl: MultipartBody.Part?,
        @Part avatarURL: MultipartBody.Part?,
        @Part frontCccdUrl: MultipartBody.Part?,
        @Part backCccdUrl: MultipartBody.Part?,
    ): Response<ApplyDoctor>

    @Multipart
    @PUT("doctor/{id}/update-profile")
    suspend fun updateClinic(
        @Path("id") id: String,
        @Part address: MultipartBody.Part,
        @Part workingHours: MultipartBody.Part,
        @Part services: MultipartBody.Part,
        @Part images: List<MultipartBody.Part>
    ): Response<Unit>


}