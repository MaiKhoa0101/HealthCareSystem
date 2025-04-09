package com.hellodoc.healthcaresystem.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
}