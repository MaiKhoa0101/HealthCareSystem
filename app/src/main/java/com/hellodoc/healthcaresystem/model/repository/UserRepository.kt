package com.hellodoc.healthcaresystem.model.repository

import android.util.Log
import com.hellodoc.healthcaresystem.api.AdminService
import com.hellodoc.healthcaresystem.api.AuthService
import com.hellodoc.healthcaresystem.api.UserService
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.User
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.UserResponse
import com.hellodoc.healthcaresystem.requestmodel.EmailRequest
import com.hellodoc.healthcaresystem.requestmodel.TokenRequest
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.OtpResponse
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
