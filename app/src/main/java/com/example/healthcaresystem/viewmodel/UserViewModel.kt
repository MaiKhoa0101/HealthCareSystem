package com.example.healthcaresystem.viewmodel

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcaresystem.api.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.healthcaresystem.model.GetUser
import com.auth0.android.jwt.JWT
import com.example.healthcaresystem.User.home.startscreen.SignIn
import com.example.healthcaresystem.model.UpdateUser

class UserViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {
    private val _users = MutableStateFlow<List<GetUser>>(emptyList())
    val users: StateFlow<List<GetUser>> get() = _users

    fun fetchUsers() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.admin.getUsers()
                if (response.isSuccessful) {
                    _users.value = response.body() ?: emptyList()
                    println("OK 1"+response.body())
                } else {
                    println("Lỗi API: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
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
                    fetchUsers() // Cập nhật danh sách sau khi chỉnh sửa
                } else {
                    Log.e("UserViewModel", "Cập nhật thất bại: ${response.errorBody()?.string()}") // Log lỗi nếu có
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Lỗi khi cập nhật user: ${e.message}") // Log lỗi
            }
        }
    }

}
