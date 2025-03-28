package com.example.healthcaresystem.viewmodel

import com.example.healthcaresystem.responsemodel.AppointmentResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcaresystem.retrofit.RetrofitInstance
import kotlinx.coroutines.launch

class AppointmentViewModel: ViewModel() {
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

}