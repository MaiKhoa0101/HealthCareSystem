package com.hellodoc.healthcaresystem.viewmodel

import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hellodoc.healthcaresystem.requestmodel.CreateAppointmentRequest
import com.hellodoc.healthcaresystem.responsemodel.AppointmentResponse
import com.hellodoc.healthcaresystem.retrofit.RetrofitInstance
import kotlinx.coroutines.launch

class AppointmentViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {
    private val _appointments = MutableStateFlow<List<AppointmentResponse>>(emptyList())
    val appoinments: StateFlow<List<AppointmentResponse>> get() = _appointments

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

}