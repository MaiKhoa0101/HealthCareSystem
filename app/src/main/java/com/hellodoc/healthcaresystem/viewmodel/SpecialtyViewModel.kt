package com.hellodoc.healthcaresystem.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hellodoc.healthcaresystem.responsemodel.Doctor
import com.hellodoc.healthcaresystem.responsemodel.GetSpecialtyResponse
import com.hellodoc.healthcaresystem.retrofit.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SpecialtyViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {
    private val _specialties = MutableStateFlow<List<GetSpecialtyResponse>>(emptyList())
    val specialties: StateFlow<List<GetSpecialtyResponse>> get() = _specialties

    fun fetchSpecialties() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.specialtyService.getSpecialties()
                if (response.isSuccessful) {
                    _specialties.value = response.body() ?: emptyList()
                    println("OK 1" + response.body())
                } else {
                    println("Lỗi API: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    // StateFlow for doctors
    private val _doctors = MutableStateFlow<List<Doctor>>(emptyList())
    val doctors: StateFlow<List<Doctor>> get() = _doctors

    // Optional: StateFlow for the specialty info
    private val _specialty = MutableStateFlow<GetSpecialtyResponse?>(null)
    val specialty: StateFlow<GetSpecialtyResponse?> get() = _specialty

    // Danh sách bác sĩ sau khi lọc
    private val _filteredDoctors = MutableStateFlow<List<Doctor>>(emptyList())
    val filteredDoctors: StateFlow<List<Doctor>> get() = _filteredDoctors

    // Lọc theo địa chỉ
    fun filterDoctorsByLocation(location: String) {
        _filteredDoctors.value = _doctors.value.filter {
            it.address?.contains(location, ignoreCase = true) == true
        }
    }

    // Xóa bộ lọc (hiển thị tất cả)
    fun clearFilter() {
        _filteredDoctors.value = _doctors.value
    }

    fun fetchSpecialtyDoctor(specialtyID: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.specialtyService.getSpecialtyById(specialtyID)
                if (response.isSuccessful) {
                    val specialtyResponse = response.body()
                    if (specialtyResponse != null) {
                        _specialty.value = specialtyResponse
                        _doctors.value = specialtyResponse.doctors
                        _filteredDoctors.value = specialtyResponse.doctors
                        //println("OK: Successfully retrieved ${specialtyResponse.doctors.size} doctors")
                    } else {
                        _doctors.value = emptyList()
                        println("API returned null response body")
                    }
                } else {
                    println("API Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                println("Exception: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}