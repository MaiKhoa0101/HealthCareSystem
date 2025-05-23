package com.hellodoc.healthcaresystem.api

import com.hellodoc.healthcaresystem.responsemodel.GetRemoteMedicalOptionResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface RemoteMedicalOptionService {
    @Headers("Content-Type: application/json")
    @GET("remote-medical-option/get-all")
    suspend fun getRemoteMedicalOptions(): Response<List<GetRemoteMedicalOptionResponse>>
}