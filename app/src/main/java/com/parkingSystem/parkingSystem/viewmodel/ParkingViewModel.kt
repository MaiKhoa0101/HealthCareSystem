package com.parkingSystem.parkingSystem.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.parkingSystem.parkingSystem.api.MakeReservationBody
import com.parkingSystem.parkingSystem.api.ReservationResponse
import com.parkingSystem.parkingSystem.api.UserApi
import com.parkingSystem.parkingSystem.responsemodel.CreateSlotEnvelope
import com.parkingSystem.parkingSystem.responsemodel.SlotData
import com.parkingSystem.parkingSystem.responsemodel.SlotDto
import com.parkingSystem.parkingSystem.responsemodel.Park
import com.parkingSystem.parkingSystem.responsemodel.Slot
import com.parkingSystem.parkingSystem.retrofit.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ParkingViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {

    lateinit var api: UserApi

    // ParkingViewModel.kt
    suspend fun bookSlot(
        parkId: String,
        slotId: String,
        userId: String,
        startTimeIso: String,
        endTimeIso: String,
        numberPlate: String?
    ): ReservationResponse = withContext(Dispatchers.IO) {

        val body = MakeReservationBody(
            parkId = parkId,
            slotId = slotId,
            userId = userId,
            startTime = startTimeIso,
            endTime = endTimeIso,
            numberPlate = numberPlate,
            paymentMethod = "cash",
            statusPayment = "unpaid"
        )

        val res = api.makeReservation(body)
        println("makeReservation -> code=${res.code()} success=${res.isSuccessful}")

        if (res.isSuccessful) {
            return@withContext ReservationResponse(
                message = "Đặt chỗ thành công!",
                reservation = null
            )
        } else {
            val err = res.errorBody()?.string()
            throw Exception("HTTP ${res.code()}: ${err ?: "Unknown error"}")
        }
    }



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

    // Kết quả tạo/ghi park (message từ server)
    private val _createParkingLotMessage = MutableStateFlow<String?>(null)
    val createParkingLotMessage: StateFlow<String?> = _createParkingLotMessage.asStateFlow()

    // ===== API calls =====

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
    fun bookSlot(parkId: String, slot_name: String) {
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

    private fun normalizeVehicle(input: String): String = when (input.trim().lowercase()) {
        "car", "oto", "ô tô", "ôto" -> "Car"
        "bike", "xe máy", "xemay", "motor" -> "Bike"
        else -> "Car"
    }

    // Map model hiển thị -> DTO request (pos_X/pos_Y là String theo BE)
    private fun mapSlotsToDto(source: List<Slot>): List<SlotDto> =
        source.map { s ->
            SlotDto(
                slotName = s.slotName.trim(),
                pos_X = s.pos_X.toString(),   // STRING theo DTO BE
                pos_Y = s.pos_Y.toString(),   // STRING theo DTO BE
                isBooked = s.isBooked
            )
        }.filter { it.slotName.isNotBlank() && it.pos_X.isNotBlank() && it.pos_Y.isNotBlank() }

    // ===== Create / Update Park (POST /document với { path, data{...} }) =====

    fun createParkingLot(
        context: Context,
        parkName: String,
        address: String,
        typeVehicleInput: String,
        priceNumber: Double,
        slotsInternal: List<Slot>             // ← nhận từ UI
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val payload = SlotData(
                    park_name = parkName,
                    address = address,
                    type_vehicle = normalizeVehicle(typeVehicleInput), // "Car" | "Bike"
                    price = priceNumber,
                    slots = mapSlotsToDto(slotsInternal)              // map sang DTO
                )

                val req = CreateSlotEnvelope(
                    path = "park",   // đồng bộ với getAllPark("park")
                    data = payload
                )

                // Log JSON để debug nhanh khi 400
                val json = Gson().toJson(req)
                Log.d("ParkingVM", "createParkingLot REQUEST = $json")

                val resp = RetrofitInstance.parking.createParkingSlot(req)
                if (resp.isSuccessful) {
                    val bodyStr = resp.body()?.toString() ?: "Tạo thành công."
                    _createParkingLotMessage.value = bodyStr
                    _successMessage.value = "Tạo/ cập nhật bãi đỗ thành công."
                    Toast.makeText(context, "Create success.", Toast.LENGTH_LONG).show()
                    Log.d("ParkingVM", "createParkingLot success: $bodyStr")
                } else {
                    val err = resp.errorBody()?.string()
                    _error.value = "Tạo thất bại"
                    Log.e("ParkingVM", "createParkingLot API error: $err")
                    Toast.makeText(context, "Create failed.", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                _error.value = "Lỗi: ${e.message}"
                Log.e("ParkingVM", "createParkingLot exception", e)
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                _isLoading.value = false
            }
        }
    }
}


