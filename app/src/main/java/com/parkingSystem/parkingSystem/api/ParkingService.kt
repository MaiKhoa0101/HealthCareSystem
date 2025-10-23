package com.parkingSystem.parkingSystem.api

import com.parkingSystem.parkingSystem.responsemodel.Park
import com.parkingSystem.parkingSystem.responsemodel.Slot
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
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

}