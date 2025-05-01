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
import com.hellodoc.healthcaresystem.retrofit.RetrofitInstance
import com.hellodoc.healthcaresystem.user.post.userId
import kotlinx.coroutines.launch

class AppointmentViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {
    private val _appointmentsUser = MutableStateFlow<List<AppointmentResponse>>(emptyList())
    val appointmentsUser: StateFlow<List<AppointmentResponse>> get() = _appointmentsUser

    private val _appointmentsDoctor= MutableStateFlow<List<AppointmentResponse>>(emptyList())
    val appointmentsDoctor: StateFlow<List<AppointmentResponse>> get() = _appointmentsDoctor

    fun fetchAppointments(){
        viewModelScope.launch{
            try{
                val response = RetrofitInstance.appointment.getAllAppointments()
                if(response.isSuccessful){
                    _appointmentsUser.value = response.body() ?: emptyList()
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
                    } else {
                        Log.e("Book", "Lỗi từ server: ${response.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    Log.e("Book", "Lỗi mạng/API: ${e.localizedMessage}")
                }
            }
        } else {
            Log.e("Book", "Token null - người dùng chưa đăng nhập?")
        }
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
                Log.e("Book", "Lỗi mạng/API: ${e.localizedMessage}")
            }
        }
    }

}