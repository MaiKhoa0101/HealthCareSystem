package com.hellodoc.healthcaresystem.presentation.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.GetRemoteMedicalOptionResponse
import com.hellodoc.healthcaresystem.retrofit.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RemoteMedicalOptionViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {
    private val _remoteMedicalOptions = MutableStateFlow<List<GetRemoteMedicalOptionResponse>>(emptyList())
    val remoteMedicalOptions: StateFlow<List<GetRemoteMedicalOptionResponse>> get() = _remoteMedicalOptions

    fun fetchRemoteMedicalOptions() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.remoteMedicalOptionService.getRemoteMedicalOptions()
                if (response.isSuccessful) {
                    _remoteMedicalOptions.value = response.body() ?: emptyList()
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