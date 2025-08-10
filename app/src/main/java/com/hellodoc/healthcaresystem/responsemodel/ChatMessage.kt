package com.hellodoc.healthcaresystem.responsemodel

data class ChatMessage(
    val message: String,
    val isUser: Boolean, // true nếu là câu hỏi, false nếu là câu trả lời
    val type: MessageType = MessageType.TEXT,
    val articleId: String? = null,
    val doctorId: String? = null
)

enum class MessageType {
    TEXT,
    ARTICLE,
    DOCTOR
}

