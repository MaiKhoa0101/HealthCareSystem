package com.hellodoc.healthcaresystem.viewmodel

import android.content.SharedPreferences
import android.util.Log
import com.hellodoc.healthcaresystem.responsemodel.AppointmentResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hellodoc.healthcaresystem.requestmodel.CreateAppointmentRequest
import com.hellodoc.healthcaresystem.requestmodel.UpdateAppointmentRequest
import com.hellodoc.healthcaresystem.retrofit.RetrofitInstance
import com.hellodoc.healthcaresystem.user.post.userId
import kotlinx.coroutines.launch

class AppointmentViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _appointmentsUser = MutableStateFlow<List<AppointmentResponse>>(emptyList())
    val appointmentsUser: StateFlow<List<AppointmentResponse>> get() = _appointmentsUser

    private val _appointmentsDoctor= MutableStateFlow<List<AppointmentResponse>>(emptyList())
    val appointmentsDoctor: StateFlow<List<AppointmentResponse>> get() = _appointmentsDoctor

    private val _appointmentSuccess = MutableStateFlow(false)
    val appointmentSuccess: StateFlow<Boolean> get() = _appointmentSuccess

    private val _appointmentError = MutableStateFlow<String?>(null)
    val appointmentError: StateFlow<String?> get() = _appointmentError

    private val _appointmentUpdated = MutableStateFlow(false)
    val appointmentUpdated: StateFlow<Boolean> get() = _appointmentUpdated
    private var currentSearchQuery: String = ""

    private val _filteredAppointments = MutableStateFlow<List<AppointmentResponse>>(emptyList())
    val filteredAppointments: StateFlow<List<AppointmentResponse>> get() = _filteredAppointments

    fun filterAppointmentsByDoctorName(doctorName: String) {
        currentSearchQuery = doctorName
        val allAppointments = _appointmentsUser.value
        _filteredAppointments.value = if (doctorName.isBlank()) {
            allAppointments
        } else {
            allAppointments.filter { it.doctor.name.contains(doctorName, ignoreCase = true) }
        }
    }

    fun fetchAppointments(){
        viewModelScope.launch{
            try{
                val response = RetrofitInstance.appointment.getAllAppointments()
                if(response.isSuccessful){
                    _appointmentsUser.value = response.body() ?: emptyList()
                    filterAppointmentsByDoctorName(currentSearchQuery)
                } else {
                    println("Lỗi API: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    fun getAppointmentUser(id: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                println("ID nhan duoc để lấy appointment: "+id)
                val result = RetrofitInstance.appointment.getAppointmentUser(id)
                if(result.isSuccessful){
                    _appointmentsUser.value = result.body() ?: emptyList()
                } else {
                    println("Lỗi API: ${result.errorBody()?.string()}")
                }

            } catch (e: Exception) {
                println("Lỗi ở getappointment")
                Log.e("Appointment", "Lỗi khi lấy appointmentUser: ${e.message}")
            } finally {
                _isLoading.value = false  // Kết thúc loading dù thành công hay lỗi
            }
        }
    }

    fun getAppointmentDoctor(id: String) {
        viewModelScope.launch {
            try {
                println("ID nhan duoc để lấy appointment: "+id)
                val result = RetrofitInstance.appointment.getAppointmentDoctor(id)
                if(result.isSuccessful){
                    _appointmentsDoctor.value = result.body() ?: emptyList()
                } else {
                    println("Lỗi API: ${result.errorBody()?.string()}")
                }

            } catch (e: Exception) {
                println("Lỗi ở getappointment")
                Log.e("Appointment", "Lỗi khi lấy appointmentDoc: ${e.message}")
            }
        }
    }

    fun createAppointment(createAppointmentRequest: CreateAppointmentRequest) {
        val token = sharedPreferences.getString("access_token", null)
        if (token != null) {
            viewModelScope.launch {
                try {
                    val response = RetrofitInstance.appointment.createAppointment(token, createAppointmentRequest)
                    if (response.isSuccessful) {
                        val result = response.body()
                        Log.d("Book", "Thành công: ${result?.message}")
                        _appointmentSuccess.value = true
                        _appointmentError.value = null
                    } else {
                        val errorMsg = response.errorBody()?.string() ?: "Lỗi không xác định từ server"
                        Log.e("Book", "Lỗi từ server: $errorMsg")
                        _appointmentError.value = errorMsg // ✅ Lưu lỗi
                    }
                } catch (e: Exception) {
                    val errorMsg = e.localizedMessage ?: "Lỗi kết nối mạng"
                    Log.e("Book", "Lỗi mạng/API: $errorMsg")
                    _appointmentError.value = errorMsg // ✅ Lưu lỗi
                }
            }
        } else {
            val errorMsg = "Token null - người dùng chưa đăng nhập?"
            Log.e("Book", errorMsg)
            _appointmentError.value = errorMsg // ✅ Lưu lỗi
        }
    }

    fun resetAppointmentSuccess() {
        _appointmentSuccess.value = false
    }

    fun resetAppointmentError() {
        _appointmentError.value = null
    }

    fun cancelAppointment(appointmentId: String, userId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.appointment.cancelAppointment(appointmentId)
                if(response.isSuccessful) {
                    val result = response.body()
                    Log.d("Cancel", "Thành công: ${result?.message}")

                    //gọi lại api để load lại ds
                    getAppointmentUser(userId)
                    getAppointmentDoctor(userId)
                } else {
                    Log.e("Cancel", "Lỗi mạng/API: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("Cancel", "Lỗi mạng/API: ${e.localizedMessage}")
            }
        }
    }

    fun updateAppointment(appointmentId: String, appointmentData: UpdateAppointmentRequest){
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.appointment.updateAppointment(appointmentId, appointmentData)
                if(response.isSuccessful) {
                    val result = response.body()
                    Log.d("Update", "Thành công: ${result?.message}")

                    //gọi lại api để load lại ds
                    getAppointmentUser(userId)
                    getAppointmentDoctor(userId)
                } else {
                    Log.e("Update", "Lỗi mạng/API: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("Update", "Lỗi mạng/API: ${e.localizedMessage}")
            }
        }
    }

    fun deleteAppointment(appointmentId: String, userId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.appointment.deleteAppointmentById(appointmentId)
                if(response.isSuccessful) {
                    val result = response.body()
                    Log.d("Delete", "Thành công: ${result?.message}")

                    //gọi lại api để load lại ds
                    getAppointmentUser(userId)
                    getAppointmentDoctor(userId)
                } else {
                    Log.e("Delete", "Lỗi mạng/API: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("Delete", "Lỗi mạng/API: ${e.localizedMessage}")
            }
        }
    }

    fun adminDeleteAppointment(appointmentId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.appointment.deleteAppointmentById(appointmentId)
                if(response.isSuccessful) {
                    val result = response.body()
                    Log.d("Delete", "Thành công: ${result?.message}")

                    //gọi lại api để load lại ds
                    fetchAppointments()
                } else {
                    Log.e("Delete", "Lỗi mạng/API: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("Delete", "Lỗi mạng/API: ${e.localizedMessage}")
            }
        }
    }

    fun confirmAppointmentDone(appointmentId: String, userId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.appointment.confirmAppointment(appointmentId)
                if (response.isSuccessful) {
                    Log.d("Confirm", "Xác nhận đã hoàn thành")
                    getAppointmentUser(userId)
                    getAppointmentDoctor(userId)
                    _appointmentUpdated.value = true
                } else {
                    Log.e("Confirm", "Lỗi xác nhận: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("Confirm", "Lỗi mạng/API: ${e.localizedMessage}")
            }
        }
    }
    fun resetAppointmentUpdated() {
        _appointmentUpdated.value = false
    }

}