package com.parkingSystem.parkingSystem.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parkingSystem.parkingSystem.responsemodel.GetFAQItemResponse
import com.parkingSystem.parkingSystem.retrofit.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FAQItemViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {
    private val _faqItems = MutableStateFlow<List<GetFAQItemResponse>>(emptyList())
    val faqItems: StateFlow<List<GetFAQItemResponse>> get() = _faqItems

    fun fetchFAQItems() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.faqItemService.getFAQItems()
                if (response.isSuccessful) {
                    _faqItems.value = response.body() ?: emptyList()
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