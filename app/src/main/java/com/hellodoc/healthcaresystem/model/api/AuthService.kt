package com.hellodoc.healthcaresystem.api


import com.hellodoc.healthcaresystem.requestmodel.EmailRequest
import com.hellodoc.healthcaresystem.requestmodel.GoogleLoginRequest
import com.hellodoc.healthcaresystem.requestmodel.LoginRequest
import com.hellodoc.healthcaresystem.requestmodel.OtpVerifyRequest
import com.hellodoc.healthcaresystem.requestmodel.ResetPasswordRequest
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.LoginResponse
import com.hellodoc.healthcaresystem.requestmodel.SignUpRequest
import com.hellodoc.healthcaresystem.requestmodel.genToken
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.GenericResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.OtpResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.SignUpResponse
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
