package com.parkingSystem.parkingSystem.responsemodel

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.annotations.SerializedName

// Data classes từ BE
data class Park(
    val park_id: String = "",
    val park_name: String = "",
    val price: Double = 0.0,
    val type_vehicle: String = "",
    val slots: List<Slot> = emptyList(),
    val address: String = ""
)

data class Slot(
    val slotName: String = "",
    val slot_id: String = "",
    val isBooked: Boolean = false,
    @SerializedName("pos_X")
    val pos_X: Int = 0,  // String to Int
    @SerializedName("pos_Y")
    val pos_Y: Int = 0   // String to Int
)

// Enum cho trạng thái hiển thị
enum class SpotStatus {
    AVAILABLE,
    OCCUPIED,
    BLOCKED
}


// Search period data class
data class SearchPeriod(
    @SerializedName("from")
    val from: String,

    @SerializedName("to")
    val to: String,

    @SerializedName("numberOfDays")
    val numberOfDays: Int
)

// Available slot for each day
data class AvailableSlot(
    @SerializedName("date")
    val date: String,

    @SerializedName("dayOfWeek")
    val dayOfWeek: Int, // 0 = Sunday, 1 = Monday, etc.

    @SerializedName("dayName")
    val dayName: String, // "Sunday", "Monday", etc.

    @SerializedName("displayDate")
    val displayDate: String, // "Saturday, June 28, 2025"

    @SerializedName("slots")
    val slots: List<TimeSlot>,

    @SerializedName("totalSlots")
    val totalSlots: Int
)

// Individual time slot
data class TimeSlot(
    @SerializedName("workingHourId")
    val workingHourId: String, // "7-7-30"

    @SerializedName("time")
    val time: String, // "07:30"

    @SerializedName("hour")
    val hour: Int, // 7

    @SerializedName("minute")
    val minute: Int, // 30

    @SerializedName("displayTime")
    val displayTime: String // "7:30 AM"
)

// Extension functions for convenience
@RequiresApi(Build.VERSION_CODES.O)
fun AvailableSlot.toLocalDate(): java.time.LocalDate? {
    return try {
        java.time.LocalDate.parse(this.date)
    } catch (e: Exception) {
        null
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun TimeSlot.toLocalTime(): java.time.LocalTime? {
    return try {
        java.time.LocalTime.of(this.hour, this.minute)
    } catch (e: Exception) {
        null
    }
}

// Helper function to check if a slot is available
@RequiresApi(Build.VERSION_CODES.O)
fun TimeSlot.isAvailable(): Boolean {
    val currentTime = java.time.LocalTime.now()
    val currentDate = java.time.LocalDate.now()

    // Add your business logic here
    return true
}

data class CreateParkingRequest(
    val park_name: String,
    val address: String,
    val type_vehicle: String,
    val price: Double,
    val slots: List<Slot>
)

data class CreateSlotEnvelope(
    val path: String,          // example: "park"
    val data: SlotData
)

data class SlotData(
    @SerializedName("park_name") val park_name: String,
    val address: String,
    @SerializedName("type_vehicle") val type_vehicle: String,
    val price: Double,
    val slots: List<SlotDto>
)

data class SlotDto(
    val slotName: String,
    @SerializedName("pos_X") val pos_X: String,
    @SerializedName("pos_Y") val pos_Y: String,
    val isBooked: Boolean
)