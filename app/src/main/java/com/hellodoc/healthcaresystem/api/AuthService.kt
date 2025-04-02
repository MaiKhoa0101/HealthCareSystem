package com.example.healthcaresystem.api


import com.example.healthcaresystem.requestmodel.LoginRequest
import com.example.healthcaresystem.responsemodel.LoginResponse
import com.example.healthcaresystem.requestmodel.SignUpRequest
import com.example.healthcaresystem.responsemodel.SignUpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("auth/signup")
    suspend fun signUp(@Body request: SignUpRequest): Response<SignUpResponse> //trả về một đối tượng SignUpResponse

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}
