package com.parkingSystem.parkingSystem.api

import com.parkingSystem.parkingSystem.responsemodel.Park
import com.parkingSystem.parkingSystem.responsemodel.Slot
import retrofit2.Response
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
    @GET("user/get-all-parking-slots/available/:pathName")
    suspend fun getAllParkAvailable(): Response<List<Park>>

    @Headers("Content-Type: application/json")
    @GET("user/get-all-parking-slots/:pathName")
    suspend fun getAllPark(): Response<List<Park>>

    @Headers("Content-Type: application/json")
    @GET("user/get-all-parking-slots/available/:pathName")
    suspend fun getParkById(): Response<Park>

    @Headers("Content-Type: application/json")
    @GET("user/get-by-id/{id}")
    suspend fun getSlotsByParkId(@Path("id") doctorId: String): Response<List<Slot>>

}