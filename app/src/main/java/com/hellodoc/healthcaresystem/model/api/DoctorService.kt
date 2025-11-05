package com.hellodoc.healthcaresystem.api

import com.hellodoc.healthcaresystem.requestmodel.ModifyClinicRequest
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.ApplyDoctor
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.DoctorAvailableSlotsResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.GetDoctorResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.PendingDoctorResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.ReturnPendingDoctorResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface DoctorService {
    @Headers("Content-Type: application/json")
    @GET("doctor/get-all")
    suspend fun getDoctors(): Response<List<GetDoctorResponse>>

    @Headers("Content-Type: application/json")
    @GET("doctor/get-by-id/{id}")
    suspend fun getDoctorById(@Path("id") doctorId: String): Response<GetDoctorResponse>

    @Multipart
    @PATCH("doctor/apply-for-doctor/{id}")
    suspend fun applyForDoctor(
        @Path("id") id: String,
        @Part license: MultipartBody.Part,
        @Part specialty: MultipartBody.Part,
        @Part CCCD: MultipartBody.Part,
        @Part address: MultipartBody.Part,
        @Part licenseUrl: MultipartBody.Part?,
        @Part faceUrl: MultipartBody.Part?,
        @Part avatarURL: MultipartBody.Part?,
        @Part frontCccdUrl: MultipartBody.Part?,
        @Part backCccdUrl: MultipartBody.Part?,
    ): Response<ApplyDoctor>

    @Multipart
    @POST("doctor/{id}/updateclinic")
    suspend fun updateClinic(
        @Path("id") id: String,
        @Part address: MultipartBody.Part,
        @Part description: MultipartBody.Part,
        @Part workingHours: MultipartBody.Part,
        @Part oldWorkingHours: MultipartBody.Part,
        @Part services: MultipartBody.Part,
        @Part images: List<MultipartBody.Part>,
        @Part oldService:MultipartBody.Part,
        @Part hasHomeService:MultipartBody.Part,
        @Part isClinicPaused:MultipartBody.Part
    ): Response<ModifyClinicRequest>

    @GET("doctor/pending-doctors")
    suspend fun getPendingDoctor(): Response<List<PendingDoctorResponse>>

    @GET("doctor/pending-doctor/{id}")
    suspend fun getPendingDoctorById(@Path("id") id: String): Response<PendingDoctorResponse>

    @DELETE("doctor/pending-doctor/{id}")
    suspend fun deletePendingDoctorById(@Path("id") id: String): Response<ReturnPendingDoctorResponse>

    @PATCH("doctor/verify-doctor/{id}")
    suspend fun verifyDoctor(@Path("id") id: String): Response<ReturnPendingDoctorResponse>

    @GET("doctor/getAvailableWorkingTime/{id}")
    suspend fun fetchAvailableSlots(@Path("id") id: String): Response<DoctorAvailableSlotsResponse>

    @GET("doctor/get-by-specialty-name")
    suspend fun getDoctorBySpecialtyName(
        @Query("name") specialtyName: String
    ): Response<List<GetDoctorResponse>>

    @GET("doctor/search-by-specialty")
    suspend fun getDoctorBySpecialNameForAI(
        @Query("query") specialtyName: String
    ): Response<List<GetDoctorResponse>>

    @GET("doctor/doctorName/{name}")
    suspend fun getDoctorByName(@Path("name") name: String): Response<List<GetDoctorResponse>>

}