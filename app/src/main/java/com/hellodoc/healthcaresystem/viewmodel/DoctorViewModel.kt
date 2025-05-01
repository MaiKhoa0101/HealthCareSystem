package com.hellodoc.healthcaresystem.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hellodoc.healthcaresystem.retrofit.RetrofitInstance
import com.hellodoc.healthcaresystem.responsemodel.GetDoctorResponse
//import com.hellodoc.healthcaresystem.responsemodel.GetDoctorResponse2
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DoctorViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {
    private val _doctors = MutableStateFlow<List<GetDoctorResponse>>(emptyList())
    val doctors: StateFlow<List<GetDoctorResponse>> get() = _doctors

    private val _doctor = MutableStateFlow<GetDoctorResponse?>(null)
    val doctor: StateFlow<GetDoctorResponse?> get() = _doctor

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    fun fetchDoctors() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.doctor.getDoctors()
                if (response.isSuccessful) {
                    _doctors.value = response.body() ?: emptyList()
                    println("OK 1" + response.body())
                } else {
                    println("Lỗi API: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun fetchDoctorById(doctorId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = RetrofitInstance.doctor.getDoctorById(doctorId)
                if (response.isSuccessful) {
                    _doctor.value = response.body()
                } else {
                    println("Lỗi lấy doctor theo id: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}