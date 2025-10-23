package com.parkingSystem.parkingSystem.api

import com.parkingSystem.parkingSystem.responsemodel.CreateParkingRequest
import com.parkingSystem.parkingSystem.responsemodel.Park
import com.parkingSystem.parkingSystem.responsemodel.Slot
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ParkingService {
    @Headers("Content-Type: application/json")
    @GET("user/get-all-parking-slots/available/park")
    suspend fun getAllParkAvailable(): Response<List<Park>>

    @Headers("Content-Type: application/json")
    @GET("manager/get-all-parking-slots/{path}")
    suspend fun getAllPark(@Path("path") namePath:String): Response<List<Park>>

    @Headers("Content-Type: application/json")
    @GET("manager/get-park-by-id/{id}")
    suspend fun getParkById(@Path("id") parkId:String): Response<Park>

    @Headers("Content-Type: application/json")
    @GET("user/get-by-id/{id}")
    suspend fun getSlotsByParkId(@Path("id") parkId: String): Response<List<Slot>>

    @Headers("Content-Type: application/json")
    @POST("manager/create-parking-slot")
    suspend fun createParkingSlot(@Body body: CreateParkingRequest): Response<Park>
}