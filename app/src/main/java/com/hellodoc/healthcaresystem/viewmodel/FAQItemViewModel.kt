package com.hellodoc.healthcaresystem.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.GetFAQItemResponse
import com.hellodoc.healthcaresystem.model.repository.FAQItemRepository
import com.hellodoc.healthcaresystem.model.retrofit.RetrofitInstance
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class FAQItemViewModel @Inject constructor(
    private val faqItemRepository: FAQItemRepository
) : ViewModel() {
    private val _faqItems = MutableStateFlow<List<GetFAQItemResponse>>(emptyList())
    val faqItems: StateFlow<List<GetFAQItemResponse>> get() = _faqItems

    fun fetchFAQItems() {
        viewModelScope.launch {
            try {
                val response = faqItemRepository.getFAQItems()
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