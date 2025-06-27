package com.hellodoc.healthcaresystem.responsemodel

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.annotations.SerializedName

data class GetDoctorResponse (
    @SerializedName("_id") val id: String, // Đổi thành `id` để dễ đọc hơn
    @SerializedName("role") val role: String,
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String,
    @SerializedName("workingHours") val workHour:List<WorkHour>,
    val address: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("password") val password: String,
    @SerializedName("specialty") val specialty: Specialty,
    @SerializedName("experience") val experience: Int?,
    @SerializedName("description") val description: String?,
    @SerializedName("avatarURL") val avatarURL: String?,
    @SerializedName("hospital") val hospital: String?,
    @SerializedName("certificates")
    val certificates: String?,
    @SerializedName("services")
    val services: List<ServiceOutput>,
    @SerializedName("patientsCount")
    val patientsCount: Int?,
    @SerializedName("ratingsCount")
    val ratingsCount: Int?,
    @SerializedName("hasHomeService")
    val hasHomeService: Boolean?,
    @SerializedName("isClinicPaused")
    val isClinicPaused: Boolean?
)

data class Specialty (
    @SerializedName("_id") val id:String,
    @SerializedName("name") val name: String
)


data class ApplyDoctor(
    val message: String
)

data class WorkHour(
    val dayOfWeek: Int,
    val hour: Int,
    val minute: Int
)

data class PendingDoctorResponse(
    @SerializedName("_id") val id: String,
    val userId: String,
    val CCCD: String,
    val license: String,
    val name: String,
    val phone: String,
    val email: String,
    val specialty: String,
    val faceUrl: String?,
    val avatarURL: String?,
    val licenseUrl: String?,
    val backCccdUrl: String?,
    val frontCccdUrl: String?
)

data class ReturnPendingDoctorResponse(
    val message: String
)
data class DoctorStatsResponse(
    val patientsCount: Int,
    val ratingsCount: Int
)

// Main response data class
data class DoctorAvailableSlotsResponse(
    @SerializedName("doctorID")
    val doctorID: String,

    @SerializedName("doctorName")
    val doctorName: String,

    @SerializedName("searchPeriod")
    val searchPeriod: SearchPeriod,

    @SerializedName("availableSlots")
    val availableSlots: List<AvailableSlot>,

    @SerializedName("totalAvailableDays")
    val totalAvailableDays: Int,

    @SerializedName("totalAvailableSlots")
    val totalAvailableSlots: Int
)

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