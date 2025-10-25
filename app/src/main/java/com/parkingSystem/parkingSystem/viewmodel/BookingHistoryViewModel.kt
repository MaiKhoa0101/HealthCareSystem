package com.parkingSystem.parkingSystem.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parkingSystem.parkingSystem.responsemodel.BookingDto
import com.parkingSystem.parkingSystem.retrofit.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class BookingHistoryViewModel(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _bookings = MutableStateFlow<List<BookingDto>>(emptyList())
    val bookings: StateFlow<List<BookingDto>> = _bookings

    fun fetchBookingHistory(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val response: Response<List<BookingDto>> =
                    RetrofitInstance.userApi.getBookingHistory(userId)

                if (response.isSuccessful) {
                    val body = response.body() ?: emptyList()
                    _bookings.value = body
                } else {
                    _error.value = "API ${response.code()}: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Lỗi tải lịch sử đặt chỗ"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
