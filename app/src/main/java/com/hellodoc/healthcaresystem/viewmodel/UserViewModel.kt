package com.hellodoc.healthcaresystem.viewmodel

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.auth0.android.jwt.JWT
import com.hellodoc.healthcaresystem.view.user.home.startscreen.SignIn
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.OtpResponse
import com.hellodoc.healthcaresystem.requestmodel.UpdateUserInput
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.User
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.UserResponse
import com.hellodoc.healthcaresystem.model.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import androidx.core.content.edit

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepo: UserRepository

) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    private val _allUser = MutableStateFlow<UserResponse?>(null)
    val allUser: StateFlow<UserResponse?> get() = _allUser

    private val _otpResult = MutableStateFlow<Result<OtpResponse>?>(null)
    val otpResult: StateFlow<Result<OtpResponse>?> get() = _otpResult

    private val _isUserLoading = MutableStateFlow(false)
    val isUserLoading: StateFlow<Boolean> = _isUserLoading

    private val _you = MutableStateFlow<User?>(null)
    val you: StateFlow<User?> = _you

    fun getAllUsers() {
        viewModelScope.launch {
            _isUserLoading.value = true
            val response = userRepo.getAllUsers()
            response?.let {
                val combined = it.doctors + it.users
                _users.value = combined
                _allUser.value = it
            }
            _isUserLoading.value = false
        }
    }

    fun getUser(id: String) {
        viewModelScope.launch {
            _isUserLoading.value = true
            try {
                _user.value  = userRepo.getUser(id)

            } catch (e: Exception) {
                Log.e("UserViewModel", "Lỗi khi lấy user: ${e.message}")
            } finally {
                _isUserLoading.value = false
            }
        }
    }

    fun getYou(context: Context){
        viewModelScope.launch {
            _isUserLoading.value = true
            try {
                println("get you được gọi")
                _you.value = userRepo.getUser(getUserAttribute("userId", context))
                println("Ket qua you: "+you.value)
                if (_you.value == null) {
                    Log.e("UserViewModel", "Không tìm thấy user hiện tại")
                }
                else Log.d("UserViewModel", "User hiện tại: ${_you.value}")
            } catch (e: Exception) {
                Log.e("UserViewModel", "Lỗi khi lấy user: ${e.message}")
            } finally {
                _isUserLoading.value = false
            }
        }
    }

    fun sendFcmToken(userId: String, role: String, token: String) {
        viewModelScope.launch {
            try {
                val res = userRepo.updateFcmToken(userId, token, role)
                if (res.isSuccessful)
                    Log.d("FCM", "Gửi token thành công")
                else Log.e("FCM", "Gửi token thất bại: ${res.code()}")
            } catch (e: Exception) {
                Log.e("FCM", "Lỗi gửi FCM: ${e.message}")
            }
        }
    }

    fun requestOtp(email: String) {
        viewModelScope.launch {
            _otpResult.value = userRepo.requestOtp(email)
        }
    }

    fun deleteUser(id: String) {
        viewModelScope.launch {
            try {
                val res = userRepo.deleteUser(id)
                if (res.isSuccessful) {
                    Log.d("UserViewModel", "Xóa thành công")
                    getAllUsers()
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Lỗi khi xóa user: ${e.message}")
            }
        }
    }

    private val _updateSuccess = MutableStateFlow<Boolean?>(null)
    val updateSuccess: StateFlow<Boolean?> = _updateSuccess
    private val _isUpdating = MutableStateFlow(false)
    val isUpdating: StateFlow<Boolean> = _isUpdating

    fun updateUser(id: String, updatedUser: UpdateUserInput, context: Context){
        viewModelScope.launch {
            try {
                _isUserLoading.value = true
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
                val name = MultipartBody.Part.createFormData(
                    "name", updatedUser.name
                )
                val email = MultipartBody.Part.createFormData(
                    "email",
                    updatedUser.email
                )
                val phone = MultipartBody.Part.createFormData(
                    "phone",
                    updatedUser.phone
                )
                val address = MultipartBody.Part.createFormData(
                    "address",
                    updatedUser.address
                )
                val password = MultipartBody.Part.createFormData(
                    "password",
                    updatedUser.password!!
                )
                val response = userRepo.updateUserByID(
                    id, avatar, address, name, email, phone, password
                )
                if (response.isSuccessful) {
                    Log.d("UserViewModel", "Cập nhật thành công user ID: $id")
                    getAllUsers()
                    _updateSuccess.value = true
                }
                else {
                    Log.e("UserViewModel", "Cập nhật thất bại: ${response.errorBody()?.string()}")
                    _updateSuccess.value = false
                }
                _isUpdating.value = false
            } catch (e: Exception) {
                Log.e("UserViewModel", "Lỗi khi cập nhật user: ${e.message}")
                _updateSuccess.value = false }
            finally {
                _isUpdating.value = false
            }
        }
    }


    fun getUserAttribute(attribute: String, context: Context): String {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("access_token", null) ?: return "unknown"
        println("Token: $token")
        return try {
            val jwt = JWT(token)
            val result = jwt.getClaim(attribute).asString() ?: "unknown"
            println("Ket qua lay duoc tu get user $attribute:" +result)
            result
        } catch (e: Exception) {
            "unknown"
        }
    }

    fun clearToken(sharedPreferences: SharedPreferences) = sharedPreferences.edit() { remove("access_token") }

    fun logout(context: Context) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        clearToken(sharedPreferences)
        val intent = Intent(context, SignIn::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }

    // --- upload helper ---
    private fun prepareFilePart(context: Context, uri: Uri, name: String): MultipartBody.Part? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val temp = File.createTempFile("upload_", ".jpg", context.cacheDir)
            temp.outputStream().use { out -> inputStream?.copyTo(out) }
            val reqFile = temp.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData(name, temp.name, reqFile)
        } catch (e: Exception) {
            Log.e("UserViewModel", "prepareFilePart error", e)
            null
        }
    }
}

