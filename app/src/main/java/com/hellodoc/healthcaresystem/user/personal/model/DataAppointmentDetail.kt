package com.hellodoc.healthcaresystem.user.personal.model

import androidx.annotation.DrawableRes

data class DoctorInfo(
    val name: String,
    val specialization: String,
    @DrawableRes val avatar: Int,
    val pricePerHour: String
)
data class PatientInfo(
    val fullName: String,
    val gender: String,
    val dateOfBirth: String,
    val phoneNumber: String
)
data class MethodOption(
    val title: String,
    val address: String,
    val isSelected: Boolean = false
)
data class AppointmentTime(
    val date: String,
    val time: String
)
data class PriceInfo(
    val clinicPrice: Int,
    val voucherValue: Int,
    val servicePrice: Int
) {
    val totalPrice: Int
        get() = clinicPrice + servicePrice - voucherValue
}
data class AppointmentDetail(
    val doctor: DoctorInfo,
    val patient: PatientInfo,
    val methodOptions: List<MethodOption>,
    val selectedTime: AppointmentTime,
    val note: String,
    val priceInfo: PriceInfo
)
