package com.parkingSystem.parkingSystem.responsemodel

import com.parkingSystem.parkingSystem.responsemodel.AppointmentResponse.Specialty
import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(), // id luôn khác nhau
    val message: String="",
    val isUser: Boolean=true, // true nếu là câu hỏi, false nếu là câu trả lời
    val type: MessageType = MessageType.TEXT,
    val articleId: String? = null,
    val doctorId: String? = null,
    val articleImgUrl: String? = null,
    val articleAuthor: String? = null,
    val doctorName: String? = null,
    val doctorAvatar: String? = null,
    val doctorAddress: String? = null,
    val doctorPhone: String? = null,
    val doctorVerified: Boolean? = null,
    val doctorSpecialty: Specialty? = null,
    val doctorHospital: String? = null,
)

enum class MessageType {
    TEXT,
    ARTICLE,
    DOCTOR
}

