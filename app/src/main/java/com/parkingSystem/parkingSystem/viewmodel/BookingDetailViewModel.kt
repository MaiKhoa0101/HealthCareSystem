package com.parkingSystem.parkingSystem.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parkingSystem.parkingSystem.responsemodel.BookingDto
import com.parkingSystem.parkingSystem.retrofit.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class BookingDetailViewModel(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _booking = MutableStateFlow<BookingDto?>(null)
    val booking: StateFlow<BookingDto?> = _booking

    private val api = RetrofitInstance.userApi

    fun fetchBookingDetail(bookingId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val res = api.getBookingDetails(bookingId)
                if (res.isSuccessful) {
                    _booking.value = res.body()
                } else {
                    _error.value = "Không lấy được chi tiết (${res.code()})"
                }
            } catch (e: IOException) {
                _error.value = "Lỗi mạng: ${e.message}"
            } catch (e: HttpException) {
                _error.value = "Lỗi server: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun cancelBooking(bookingId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val res = api.cancelReservation(bookingId)
                if (res.isSuccessful) {
                    onSuccess()
                    fetchBookingDetail(bookingId)
                } else {
                    _error.value = "Huỷ thất bại (${res.code()})"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Huỷ thất bại"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
