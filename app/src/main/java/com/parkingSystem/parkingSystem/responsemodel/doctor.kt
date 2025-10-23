package com.parkingSystem.parkingSystem.responsemodel

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.annotations.SerializedName

// Data classes từ BE
data class Park(
    val parkId: String = "",
    val parkName: String = "",
    val price: Double = 0.0,
    val typeVehicle: String = "",
    val slots: List<Slot> = emptyList(),
    val address: String = ""
)

data class Slot(
    val slotName: String = "",
    val slotId: String = "",
    val isBooked: Boolean = false,
    @SerializedName("pos_X")
    val pos_x: Int = 0,  // Đổi từ String sang Int
    @SerializedName("pos_Y")
    val pos_y: Int = 0   // Đổi từ String sang Int
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
    val from: String, // Format: "2025-06-26"

    @SerializedName("to")
    val to: String, // Format: "2025-07-02"

    @SerializedName("numberOfDays")
    val numberOfDays: Int
)

// Available slot for each day
data class AvailableSlot(
    @SerializedName("date")
    val date: String, // Format: "2025-06-28"

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
    val parkName: String,
    val address: String,
    val typeVehicle: String,
    val price: Double,
    val slots: List<Slot>
)
