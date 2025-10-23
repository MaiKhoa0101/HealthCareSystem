package com.parkingSystem.parkingSystem.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.parkingSystem.parkingSystem.responsemodel.CreateSlotEnvelope
import com.parkingSystem.parkingSystem.responsemodel.SlotData
import com.parkingSystem.parkingSystem.responsemodel.SlotDto
import com.parkingSystem.parkingSystem.responsemodel.Park
import com.parkingSystem.parkingSystem.responsemodel.Slot
import com.parkingSystem.parkingSystem.retrofit.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ParkingViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {

    // ===== State =====
    private val _parks = MutableStateFlow<List<Park>>(emptyList())
    val parks: StateFlow<List<Park>> = _parks.asStateFlow()

    private val _currentPark = MutableStateFlow<Park?>(null)
    val currentPark: StateFlow<Park?> = _currentPark.asStateFlow()

    private val _slots = MutableStateFlow<List<Slot>>(emptyList())
    val slots: StateFlow<List<Slot>> = _slots.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

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
                Log.d("ParkingVM", "Fetching parks...")
                val response = RetrofitInstance.parking.getAllPark("park")
                if (response.isSuccessful && response.body() != null) {
                    _parks.value = response.body()!!
                } else {
                    _error.value = "Không thể tải danh sách bãi đậu xe"
                    Log.e("ParkingVM", "fetchAllParksAvailable error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _error.value = "Lỗi: ${e.message}"
                Log.e("ParkingVM", "fetchAllParksAvailable exception", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchParkById(parkId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                Log.d("ParkingVM", "fetchParkById: $parkId")
                val response = RetrofitInstance.parking.getParkById(parkId)
                if (response.isSuccessful && response.body() != null) {
                    val park = response.body()!!
                    _currentPark.value = park
                    _slots.value = park.slots
                } else {
                    _error.value = "Không thể tải thông tin bãi đậu xe"
                    Log.e("ParkingVM", "fetchParkById error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _error.value = "Lỗi: ${e.message}"
                Log.e("ParkingVM", "fetchParkById exception", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchSlotsByPark(parkId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                Log.d("ParkingVM", "fetchSlotsByPark: $parkId")
                val response = RetrofitInstance.parking.getSlotsByParkId(parkId)
                if (response.isSuccessful && response.body() != null) {
                    _slots.value = response.body()!!
                } else {
                    _error.value = "Không thể tải danh sách chỗ đậu xe"
                    Log.e("ParkingVM", "fetchSlotsByPark error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _error.value = "Lỗi: ${e.message}"
                Log.e("ParkingVM", "fetchSlotsByPark exception", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ===== Helpers =====

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
                posX = s.pos_x.toString(),   // STRING theo DTO BE
                posY = s.pos_y.toString(),   // STRING theo DTO BE
                isBooked = s.isBooked
            )
        }.filter { it.slotName.isNotBlank() && it.posX.isNotBlank() && it.posY.isNotBlank() }

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
                    parkName = parkName,
                    address = address,
                    typeVehicle = normalizeVehicle(typeVehicleInput), // "Car" | "Bike"
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

    // ===== (Placeholder) các hàm khác =====

    fun bookSlot(parkId: String, slotId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                Log.d("ParkingVM", "bookSlot park=$parkId slot=$slotId")
                // TODO: call booking API
            } catch (e: Exception) {
                _error.value = "Lỗi: ${e.message}"
                Log.e("ParkingVM", "bookSlot exception", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateSlotStatus(parkId: String, slotId: String, isBooked: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                Log.d("ParkingVM", "updateSlotStatus park=$parkId slot=$slotId -> $isBooked")
                // TODO: call update slot API
            } catch (e: Exception) {
                _error.value = "Lỗi: ${e.message}"
                Log.e("ParkingVM", "updateSlotStatus exception", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun cancelBooking(parkId: String, slotId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                Log.d("ParkingVM", "cancelBooking park=$parkId slot=$slotId")
                // TODO: call cancel API
            } catch (e: Exception) {
                _error.value = "Lỗi: ${e.message}"
                Log.e("ParkingVM", "cancelBooking exception", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ===== State utils =====

    fun clearError() {
        _error.value = null
    }

    fun clearSuccessMessage() {
        _successMessage.value = null
    }

    fun resetState() {
        _currentPark.value = null
        _slots.value = emptyList()
        _error.value = null
        _successMessage.value = null
        _createParkingLotMessage.value = null
    }
}
