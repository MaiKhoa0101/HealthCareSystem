package com.parkingSystem.parkingSystem.api

import com.parkingSystem.parkingSystem.responsemodel.Park
import com.parkingSystem.parkingSystem.responsemodel.Slot
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface ParkingService {
    @Headers("Content-Type: application/json")
    @GET("user/get-all-parking-slots/available/:pathName")
    suspend fun getAllParkAvailable(): Response<List<Park>>

    @Headers("Content-Type: application/json")
    @GET("user/get-all-parking-slots/:pathName")
    suspend fun getAllPark(): Response<List<Park>>

    @Headers("Content-Type: application/json")
    @GET("user/get-all-parking-slots/available/:pathName")
    suspend fun getParkById(@Path("id") parkId:String): Response<Park>

    @Headers("Content-Type: application/json")
    @GET("user/get-by-id/{id}")
    suspend fun getSlotsByParkId(@Path("id") parkId: String): Response<List<Slot>>

}