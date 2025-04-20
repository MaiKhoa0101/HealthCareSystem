package com.hellodoc.healthcaresystem.viewmodel

import android.content.SharedPreferences
import android.util.Log
import com.hellodoc.healthcaresystem.responsemodel.AppointmentResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hellodoc.healthcaresystem.requestmodel.CreateAppointmentRequest
import com.hellodoc.healthcaresystem.retrofit.RetrofitInstance
import kotlinx.coroutines.launch

class AppointmentViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {
    private val _appointments = MutableStateFlow<List<AppointmentResponse>>(emptyList())
    val appointments: StateFlow<List<AppointmentResponse>> get() = _appointments

    fun fetchAppointments(){
        viewModelScope.launch{
            try{
                val response = RetrofitInstance.appointment.getAllAppointments()
                if(response.isSuccessful){
                    _appointments.value = response.body() ?: emptyList()
                    println("OK 1"+ response.body())
                } else {
                    println("Lỗi API: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    fun createAppointment(createAppointmentRequest: CreateAppointmentRequest) {
        viewModelScope.launch {
            try {
                val accessToken = sharedPreferences.getString("access_token", null) ?: ""

                if (accessToken.isEmpty()) {
                    Log.e("AppointmentViewModel", "Token không tồn tại hoặc rỗng")
                    return@launch
                }

                Log.d("AccessTokenCheck", "Access Token: $accessToken")

                val response = RetrofitInstance.appointment.createAppointment(
                    accessToken,
                    createAppointmentRequest
                )

                if (response.isSuccessful) {
                    val result = response.body()
                    Log.d("Book", "Đặt lịch thành công: ${result?.message}")
                } else {
                    Log.e("Book", "Lỗi từ server: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("Book", "Lỗi mạng/API: ${e.localizedMessage}")
            }
        }
    }


}