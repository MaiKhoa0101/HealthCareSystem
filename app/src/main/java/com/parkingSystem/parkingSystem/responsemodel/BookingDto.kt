package com.parkingSystem.parkingSystem.responsemodel

data class BookingDto(
    val id: String? = null,
    val userId: String? = null,
    val parkId: String? = null,
    val parkName: String? = null,
    val address: String? = null,

    val slotId: String? = null,
    val slotName: String? = null,
    val numberPlate: String? = null,
    val createdAt: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,

    val status: String? = null,
    val price: Double? = null,
    val type_vehicle: String? = null
)
