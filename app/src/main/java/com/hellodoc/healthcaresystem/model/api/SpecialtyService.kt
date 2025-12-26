package com.hellodoc.healthcaresystem.model.api

import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.GetSpecialtyResponse
import com.hellodoc.healthcaresystem.requestmodel.GetDoctorBySpecialtyNameRequest
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface SpecialtyService {
    @Headers("Content-Type: application/json")
    @GET("specialty/get-all")
    suspend fun getSpecialties(): Response<List<GetSpecialtyResponse>>

    @GET("specialty/doctors/{id}")
    suspend fun getSpecialtyById(@Path("id") specialtyId: String): Response<GetSpecialtyResponse>

    @Multipart
    @POST("specialty/create")
    suspend fun createSpecialty(
        @Part name: MultipartBody.Part,
        @Part icon: MultipartBody.Part,
        @Part description: MultipartBody.Part
    ): Response<GetSpecialtyResponse>

    @Headers("Content-Type: application/json")
    @POST("specialty/specialty-by-name")
    suspend fun getSpecialtyByName(@Body dto: GetDoctorBySpecialtyNameRequest): Response<GetSpecialtyResponse>
}