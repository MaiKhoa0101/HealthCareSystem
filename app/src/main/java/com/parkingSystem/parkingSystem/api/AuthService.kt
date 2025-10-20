package com.parkingSystem.parkingSystem.api


import com.parkingSystem.parkingSystem.requestmodel.EmailRequest
import com.parkingSystem.parkingSystem.requestmodel.GoogleLoginRequest
import com.parkingSystem.parkingSystem.requestmodel.LoginRequest
import com.parkingSystem.parkingSystem.requestmodel.OtpVerifyRequest
import com.parkingSystem.parkingSystem.requestmodel.ResetPasswordRequest
import com.parkingSystem.parkingSystem.responsemodel.LoginResponse
import com.parkingSystem.parkingSystem.requestmodel.SignUpRequest
import com.parkingSystem.parkingSystem.requestmodel.genToken
import com.parkingSystem.parkingSystem.responsemodel.GenericResponse
import com.parkingSystem.parkingSystem.responsemodel.OtpResponse
import com.parkingSystem.parkingSystem.responsemodel.SignUpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("auth/signup")
    suspend fun signUp(@Body request: SignUpRequest): Response<SignUpResponse> //trả về một đối tượng SignUpResponse

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/login-google")
    suspend fun loginGoogle(@Body request: GoogleLoginRequest): Response<LoginResponse>

    @POST("auth/generate-token")
    suspend fun generateToken(@Body request: genToken): Response<LoginResponse>

    @POST("auth/request-otp")
    suspend fun requestOtp(@Body request: EmailRequest): Response<OtpResponse>

    @POST("auth/request-otp-signup")
    suspend fun requestOtpSignUp(@Body request: EmailRequest): Response<OtpResponse>

    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body request: OtpVerifyRequest): Response<GenericResponse>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<GenericResponse>
}
