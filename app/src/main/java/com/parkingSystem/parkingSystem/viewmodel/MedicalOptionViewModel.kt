package com.parkingSystem.parkingSystem.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parkingSystem.parkingSystem.responsemodel.GetMedicalOptionResponse
import com.parkingSystem.parkingSystem.retrofit.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MedicalOptionViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {
    private val _medicalOptions = MutableStateFlow<List<GetMedicalOptionResponse>>(emptyList())
    val medicalOptions: StateFlow<List<GetMedicalOptionResponse>> get() = _medicalOptions

    fun fetchMedicalOptions() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.medicalOptionService.getMedicalOptions()
                if (response.isSuccessful) {
                    _medicalOptions.value = response.body() ?: emptyList()
                    println("OK 1" + response.body())
                } else {
                    println("Lỗi API: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}