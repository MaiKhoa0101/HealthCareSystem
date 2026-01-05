package com.hellodoc.healthcaresystem.model.repository

import android.util.Log
import com.hellodoc.healthcaresystem.model.api.AdminService
import com.hellodoc.healthcaresystem.model.api.AuthService
import com.hellodoc.healthcaresystem.model.api.UserService
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.*
import com.hellodoc.healthcaresystem.requestmodel.*
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userService: UserService,
    private val adminService: AdminService,
    private val authenService: AuthService
) {

    suspend fun getUser(id: String): User = userService.getUser(id)

    suspend fun getAllUsers(): UserResponse? {
        return try {
            val res = adminService.getAllUser()
            if (res.isSuccessful) res.body() else null
        } catch (e: Exception) {
            Log.e("UserRepository", "getAllUsers failed: ${e.message}")
            null
        }
    }

    suspend fun updateFcmToken(userId: String, token: String, model: String) =
        userService.updateFcmToken(userId, TokenRequest(token, model))

    suspend fun deleteUser(userId: String) = adminService.deleteUser(userId)

    suspend fun requestOtp(email: String): Result<OtpResponse> {
        return try {
            val response = authenService.requestOtp(EmailRequest(email))
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Gửi OTP thất bại"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val response = authenService.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Đăng nhập thất bại"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginGoogle(idToken: String, phone: String = ""): Result<LoginResponse> {
        return try {
            val response = authenService.loginGoogle(GoogleLoginRequest(idToken, phone))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Đăng nhập Google thất bại"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUp(username: String, email: String, phone: String, password: String, role: String): Result<SignUpResponse> {
        return try {
            val response = authenService.signUp(SignUpRequest(username, email, phone, password, role))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Đăng ký thất bại"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun requestOtpSignUp(email: String): Result<OtpResponse> {
        return try {
            val response = authenService.requestOtpSignUp(EmailRequest(email))
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Gửi OTP thất bại"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyOtp(email: String, otp: String): Result<GenericResponse> {
        return try {
            val response = authenService.verifyOtp(OtpVerifyRequest(email, otp))
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Xác minh thất bại"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetPassword(email: String, newPassword: String): Result<GenericResponse> {
        return try {
            val response = authenService.resetPassword(ResetPasswordRequest(email, newPassword))
            if (response.isSuccessful) Result.success(response.body()!!)
            else Result.failure(Exception("Đặt lại mật khẩu thất bại"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserByID(
        id: String,
        avatarURL: MultipartBody.Part?,
        address: MultipartBody.Part?,
        name: MultipartBody.Part?,
        email: MultipartBody.Part?,
        phone: MultipartBody.Part?,
        password: MultipartBody.Part?
    ): Response<User> {
        return try {
            adminService.updateUserByID(
                id = id,
                avatarURL = avatarURL,
                name = name,
                email = email,
                address = address,
                phone = phone,
                password = password
            )
        } catch (e: Exception) {
            Log.e("UserRepository", "updateUserByID failed: ${e.message}")
            throw e
        }
    }
}
