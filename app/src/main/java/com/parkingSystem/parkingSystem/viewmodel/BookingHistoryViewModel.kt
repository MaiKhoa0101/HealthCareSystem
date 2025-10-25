package com.parkingSystem.parkingSystem.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parkingSystem.parkingSystem.responsemodel.BookingDto
import com.parkingSystem.parkingSystem.retrofit.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BookingHistoryViewModel(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _bookings = MutableStateFlow<List<BookingDto>>(emptyList())
    val bookings: StateFlow<List<BookingDto>> = _bookings

    private val _justCancelled = MutableStateFlow(false)
    val justCancelled: StateFlow<Boolean> = _justCancelled

    fun fetchBookingHistory(userId: String) = viewModelScope.launch {
        try {
            _isLoading.value = true
            _error.value = null

            val res = RetrofitInstance.userApi.getBookingHistory(userId)
            if (res.isSuccessful) {
                _bookings.value = res.body().orEmpty()
            } else {
                _error.value = "Không tải được lịch sử (${res.code()})"
            }
        } catch (e: Exception) {
            _error.value = e.message ?: "Lỗi tải lịch sử đặt chỗ"
        } finally {
            _isLoading.value = false
        }
    }

    fun cancelReservation(bookingId: String) = viewModelScope.launch {
        try {
            _isLoading.value = true
            _error.value = null

            val res = RetrofitInstance.userApi.cancelReservation(bookingId)
            if (!res.isSuccessful) {
                _error.value = "Huỷ thất bại (${res.code()})"
                return@launch
            }

            _bookings.value = _bookings.value.map { b ->
                if (b.id == bookingId) b.copy(status = "cancelled") else b
            }
            _justCancelled.value = true

        } catch (e: Exception) {
            _error.value = e.message ?: "Huỷ thất bại"
        } finally {
            _isLoading.value = false
        }
    }

    fun consumeJustCancelledFlag() {
        _justCancelled.value = false
    }
}
