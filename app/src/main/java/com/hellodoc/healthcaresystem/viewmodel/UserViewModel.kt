package com.hellodoc.healthcaresystem.viewmodel

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hellodoc.healthcaresystem.retrofit.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.auth0.android.jwt.JWT
import com.hellodoc.healthcaresystem.requestmodel.TokenRequest
import com.hellodoc.healthcaresystem.user.home.startscreen.SignIn
import com.hellodoc.healthcaresystem.requestmodel.UpdateUser
import com.hellodoc.healthcaresystem.responsemodel.User
import com.hellodoc.healthcaresystem.responsemodel.UserResponse

class UserViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {


    //Bien lay 1 user
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> get() = _user

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> get() = _users

    private val _allUser = MutableStateFlow<UserResponse?>(null)
    val allUser: StateFlow<UserResponse?> get() = _allUser


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



    fun getUser(id: String) {
        viewModelScope.launch {
            try {
                val result = RetrofitInstance.userService.getUser(id)
                _user.value = result
                println("OK fetch user: $result")
            } catch (e: Exception) {
                Log.e("UserViewModel", "Lỗi khi lấy user: ${e.message}")
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

    // Update user
    fun updateUser(id: String, updatedUser: UpdateUser) {
        viewModelScope.launch {
            try {
                Log.d("UserViewModel", "Đang cập nhật user có ID: ${id}") // Log ID user

                val response = RetrofitInstance.admin.updateUserByID(id, updatedUser)
                if (response.isSuccessful) {
                    Log.d("UserViewModel", "Cập nhật thành công user ID: ${id}") // Log khi cập nhật thành công
                    getAllUsers() // Cập nhật danh sách sau khi chỉnh sửa
                } else {
                    Log.e("UserViewModel", "Cập nhật thất bại: ${response.errorBody()?.string()}") // Log lỗi nếu có
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Lỗi khi cập nhật user: ${e.message}") // Log lỗi
            }
        }
    }

    fun sendFcmToken(userId: String, token: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.userService.updateFcmToken(userId, TokenRequest(token))
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
}


