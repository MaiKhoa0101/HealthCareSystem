package com.hellodoc.healthcaresystem.responsemodel

import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(), // id luôn khác nhau
    val message: String,
    val isUser: Boolean, // true nếu là câu hỏi, false nếu là câu trả lời
    val type: MessageType = MessageType.TEXT,
    val articleId: String? = null,
    val doctorId: String? = null,
    val articleImgUrl: String? = null,
    val articleAuthor: String? = null
)

enum class MessageType {
    TEXT,
    ARTICLE,
    DOCTOR
}

