package com.parkingSystem.parkingSystem.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.util.Log.e
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.parkingSystem.parkingSystem.responsemodel.CreateParkingRequest
import com.parkingSystem.parkingSystem.responsemodel.Park
import com.parkingSystem.parkingSystem.responsemodel.Slot
import com.parkingSystem.parkingSystem.retrofit.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class ParkingViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {

    // State cho danh sách bãi đậu xe
    private val _parks = MutableStateFlow<List<Park>>(emptyList())
    val parks: StateFlow<List<Park>> = _parks.asStateFlow()

    // State cho bãi đậu xe hiện tại
    private val _currentPark = MutableStateFlow<Park?>(null)
    val currentPark: StateFlow<Park?> = _currentPark.asStateFlow()

    // State cho các slot của bãi đậu xe
    private val _slots = MutableStateFlow<List<Slot>>(emptyList())
    val slots: StateFlow<List<Slot>> = _slots.asStateFlow()

    // State loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // State error
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // State success message
    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    fun fetchAllParksAvailable() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                println("Fetching parks...")
                val token = sharedPreferences.getString("auth_token", "") ?: ""
                val response = RetrofitInstance.parking.getAllPark("park")

                if (response.isSuccessful) {
                    println("API trả về thành công" + response.body().toString())
                    _parks.value = response.body()!!
                } else {
                    println("API khoong trả về thành công" + response.body().toString())
                    _error.value = "Không thể tải danh sách bãi đậu xe"
                }
            } catch (e: Exception) {
                _error.value = "Lỗi: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Lấy thông tin chi tiết một bãi đậu xe theo ID
     */
    fun fetchParkById(parkId: String){
        viewModelScope.launch {
            _isLoading.value = true

            try {
                println ("vao duoc fetch park id")
                val response = RetrofitInstance.parking.getParkById(parkId)

                if (response.isSuccessful && response.body() != null) {
                    _currentPark.value = response.body()
                    println("fetchParkById: " + response.body().toString())
                    _slots.value = response.body()!!.slots
                } else {
                    println("fetchParkById: " + response.body().toString())
                    _error.value = "Không thể tải thông tin bãi đậu xe"
                }
            } catch (e: Exception) {
                _error.value = "Lỗi: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Lấy danh sách slot theo parkId
     */
    fun fetchSlotsByPark(parkId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val token = sharedPreferences.getString("auth_token", "") ?: ""
                val response = RetrofitInstance.parking.getSlotsByParkId(parkId)

                if (response.isSuccessful && response.body() != null) {
                    _slots.value = response.body()!!
                } else {
                    _error.value = "Không thể tải danh sách chỗ đậu xe"
                }
            } catch (e: Exception) {
                _error.value = "Lỗi: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Đặt chỗ đậu xe
     */
    fun bookSlot(parkId: String, slotId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val token = sharedPreferences.getString("auth_token", "") ?: ""

            } catch (e: Exception) {
                _error.value = "Lỗi: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Cập nhật trạng thái slot (Admin function)
     */
    fun updateSlotStatus(parkId: String, slotId: String, isBooked: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val token = sharedPreferences.getString("auth_token", "") ?: ""
            } catch (e: Exception) {
                _error.value = "Lỗi: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Hủy đặt chỗ
     */
    fun cancelBooking(parkId: String, slotId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val token = sharedPreferences.getString("auth_token", "") ?: ""
            } catch (e: Exception) {
                _error.value = "Lỗi: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Clear success message
     */
    fun clearSuccessMessage() {
        _successMessage.value = null
    }

    /**
     * Reset state
     */
    fun resetState() {
        _currentPark.value = null
        _slots.value = emptyList()
        _error.value = null
        _successMessage.value = null
    }

    private val _createParkingLotMessage  = MutableStateFlow<Park?>(null)
    val createParkingLotMessage : StateFlow<Park?> = _createParkingLotMessage

    fun createParkingLot(data: Park, context: Context) {
        viewModelScope.launch {
            try {
                val req = CreateParkingRequest(
                    parkName = data.parkName,
                    address = data.address,
                    typeVehicle = data.typeVehicle,
                    price = data.price ?: 0.0,
                    slots = data.slots
                )
                val address = MultipartBody.Part.createFormData("address", data.address)
                val typeVehicle =
                    MultipartBody.Part.createFormData("type_vehicle", data.typeVehicle)
                val price =
                    MultipartBody.Part.createFormData("price", (data.price ?: 0.0).toString())

                // slotsJson (List<Slot> -> String)
                val slotsJsonStr = Gson().toJson(data.slots)
                val slotsBody = slotsJsonStr.toRequestBody("application/json; charset=utf-8".toMediaType())

                val response = RetrofitInstance.parking.createParkingSlot(req)



                if (response.isSuccessful) {
                    val body = response.body()
                    _createParkingLotMessage.value = body

                    Toast.makeText(context, "Create success.", Toast.LENGTH_LONG).show()
                    Log.d("CreateParkingLot", "success: $body")
                } else {
                    val err = response.errorBody()?.string()
                    Toast.makeText(context, "Create failed.", Toast.LENGTH_LONG).show()
                    Log.e("CreateParkingLot", "API error: $err")
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("CreateParkingLot", "exception", e)
            }
        }
    }
}