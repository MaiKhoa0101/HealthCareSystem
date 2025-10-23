package com.parkingSystem.parkingSystem.viewmodel

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parkingSystem.parkingSystem.retrofit.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.auth0.android.jwt.JWT
import com.parkingSystem.parkingSystem.requestmodel.EmailRequest
import com.parkingSystem.parkingSystem.requestmodel.FirebaseLoginRequest
import com.parkingSystem.parkingSystem.requestmodel.TokenRequest
import com.parkingSystem.parkingSystem.user.home.startscreen.SignIn
import com.parkingSystem.parkingSystem.responsemodel.OtpResponse
import com.parkingSystem.parkingSystem.requestmodel.UpdateUserInput
import com.parkingSystem.parkingSystem.responsemodel.User
import com.parkingSystem.parkingSystem.responsemodel.UserResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class UserViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {
    //Bien lay 1 user
    private val _targetUser = MutableStateFlow<User?>(null)
    val targetUser: StateFlow<User?> get() = _targetUser

    //Bien lay 1 user
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> get() = _user

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> get() = _users

    private val _allUser = MutableStateFlow<UserResponse?>(null)
    val allUser: StateFlow<UserResponse?> get() = _allUser


    fun clearUsers() {
        _user.value = null
    }

    fun getAllUsers() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.admin.getAllUser()
                if (response.isSuccessful) {
                    response.body()?.let { userResponse ->
                        val combinedList = userResponse.doctors + userResponse.users
                        _users.value = combinedList          // <-- gán danh sách hiển thị
                        _allUser.value = userResponse        // <-- lưu đầy đủ nếu cần sau này
                    } ?: run {
                        Log.e("UserViewModel", "Response body is null")
                    }
                } else {
                    Log.e("UserViewModel", "Response failed: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Exception: ${e.message}")
            }
        }
    }

    private var _isUserLoading = MutableStateFlow(false)
    val isUserLoading: StateFlow<Boolean> get() = _isUserLoading

    fun getUser(idToken: String) {
        viewModelScope.launch {
            try {
                _isUserLoading.value = true
               ///Chưa có logic
            } catch (e: Exception) {
                Log.e("UserViewModel", "Lỗi khi lấy user: ${e.message}")
            } finally {
                _isUserLoading.value = false
            }
        }
    }

    fun getYou(id: String) {
        viewModelScope.launch {
            try {
                _isUserLoading.value = true
                val result = RetrofitInstance.userService.getUser(id)
                _targetUser.value = result
                println("OK fetch user: $result")
            } catch (e: Exception) {
                Log.e("UserViewModel", "Lỗi khi lấy user: ${e.message}")
            } finally {
                _isUserLoading.value = false
            }
        }
    }


    fun getUserNameFromToken(): String {
        val token = sharedPreferences.getString("access_token", null) ?: return "Người dùng"
        return decodeToken(token)
    }

    private fun decodeToken(token: String): String {
        return try {
            val jwt = JWT(token)
            jwt.getClaim("name").asString() ?: "Người dùng"
        } catch (e: Exception) {
            "Người dùng"
        }
    }

    fun getUserRole(): String {
        val token = sharedPreferences.getString("access_token", null) ?: return "unknown"
        return try {
            val jwt = JWT(token)
            jwt.getClaim("role").asString() ?: "unknown"
            sharedPreferences.edit().putString("role", jwt.getClaim("role").asString()).toString()
        } catch (e: Exception) {
            "unknown"
        }
    }

    fun getUserAttributeString(attribute: String): String {
        val token = sharedPreferences.getString("access_token", null) ?: return "unknown"
        return try {
            val jwt = JWT(token)
            jwt.getClaim(attribute).asString() ?: "unknown"
        } catch (e: Exception) {
            "unknown"
        }
    }

    fun getAllUserAttributeString() {
        val token = sharedPreferences.getString("access_token", null)
        if (token.isNullOrEmpty()) {
            Log.e("JWT", "Token not found")
            _user.value = null
            return
        }

        try {
            val jwt = JWT(token)
            val claims = jwt.claims.mapValues { it.value.asString() ?: "" }

            // Dùng .getOrElse để tránh null hoặc thiếu field
            val userObj = User(
                id = claims.getOrElse("id") { claims["sub"] ?: "" },
                name = claims.getOrElse("name") { "" },
                email = claims.getOrElse("email") { "" },
                phone = claims.getOrElse("phone") { "" },
                password = claims.getOrElse("password") { "" },
                address = claims.getOrElse("address") { "" },
                role = claims.getOrElse("role") { "" },
                createdAt = claims.getOrElse("createdAt") { "" },
                updatedAt = claims.getOrElse("updatedAt") { "" },
                avatarURL = claims.getOrElse("avatarURL") { "" }
            )

            _user.value = userObj
            Log.d("JWT", "Fetched user: $userObj")

        } catch (e: Exception) {
            _user.value = null
            Log.e("JWT", "Failed to parse token: ${e.message}")
        }
    }



    fun clearToken() {
        sharedPreferences.edit().remove("access_token").apply()
    }


    fun logout(context: Context) {
        clearToken() // Xóa token trước khi đăng xuất

        // Chuyển về màn hình đăng nhập
        val intent = Intent(context, SignIn::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }

    private fun prepareFilePart(
        context: Context,
        fileUri: Uri,
        partName: String
    ): MultipartBody.Part? {
        return try {
            val inputStream = context.contentResolver.openInputStream(fileUri)
            val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
            tempFile.outputStream().use { outputStream ->
                inputStream?.copyTo(outputStream)
            }

            val requestFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData(partName, tempFile.name, requestFile)
        } catch (e: Exception) {
            Log.e("UserViewModel", "Error preparing file part", e)
            null
        }
    }


    private val _updateSuccess = MutableStateFlow<Boolean?>(null)
    val updateSuccess: StateFlow<Boolean?> = _updateSuccess
    private val _isUpdating = MutableStateFlow(false)
    val isUpdating: StateFlow<Boolean> = _isUpdating

    fun resetUpdateStatus() {
        _updateSuccess.value = false
    }
    fun updateUser(id: String, updatedUser: UpdateUserInput, context: Context) {
        viewModelScope.launch {
            try {
                _isUpdating.value = true
                Log.d("UserViewModel", "Đang cập nhật user có ID: ${id}")
                println("===== Thông tin gửi lên =====")
                println("Avatar URL: ${updatedUser.avatarURL ?: "Không có"}")
                println("Name: ${updatedUser.name}")
                println("Address: ${updatedUser.address}")
                println("Email: ${updatedUser.email}")
                println("Phone: ${updatedUser.phone}")
                println("Password: ${updatedUser.password}")

                val avatar = updatedUser.avatarURL?.let {
                    prepareFilePart(context, it, "avatarURL")
                }
                val name = MultipartBody.Part.createFormData("name", updatedUser.name)
                val email = MultipartBody.Part.createFormData("email", updatedUser.email)
                val phone = MultipartBody.Part.createFormData("phone", updatedUser.phone)
                val address = MultipartBody.Part.createFormData("address", updatedUser.address)
                val password = MultipartBody.Part.createFormData("password", updatedUser.password!!)

                val response = RetrofitInstance.admin.updateUserByID(
                    id,
                    avatar,
                    address,
                    name,
                    email,
                    phone,
                    password
                )

                if (response.isSuccessful) {
                    Log.d("UserViewModel", "Cập nhật thành công user ID: $id")
                    getAllUsers()
                    _updateSuccess.value = true
                } else {
                    Log.e("UserViewModel", "Cập nhật thất bại: ${response.errorBody()?.string()}")
                    _updateSuccess.value = false
                }
                _isUpdating.value = false
            } catch (e: Exception) {
                Log.e("UserViewModel", "Lỗi khi cập nhật user: ${e.message}")
                _updateSuccess.value = false
            }
            finally {
                _isUpdating.value = false
            }
        }
    }

    fun sendFcmToken(userId: String, userModel: String, token: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.userService.updateFcmToken(userId, TokenRequest(token, userModel))
                if (response.isSuccessful) {
                    Log.d("FCM", "Đã gửi fcmToken lên server")
                } else {
                    Log.e("FCM", "Lỗi gửi fcmToken: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("FCM", "Lỗi: ${e.localizedMessage}")
            }
        }
    }

    private val _otpResult = MutableStateFlow<Result<OtpResponse>?>(null)
    val otpResult: StateFlow<Result<OtpResponse>?> get() = _otpResult

    fun requestOtp(email: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.requestOtp(EmailRequest(email))
                if (response.isSuccessful) {
                    _otpResult.value = Result.success(response.body()!!)
                } else {
                    _otpResult.value = Result.failure(Exception("Gửi OTP thất bại"))
                }
            } catch (e: Exception) {
                _otpResult.value = Result.failure(e)
            }
        }
    }

    fun deleteUser(userID: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.admin.deleteUser(userID)
                if(response.isSuccessful) {
                    Log.d("Xóa User", "Thành công")
                    getAllUsers()
                }else {
                    Log.e("xóa user", "thất bại")
                }
            } catch (e: Exception) {
                Log.e("xóa user", "Lỗi khi xóa user: ${e.message}")
            }
        }
    }
}

