package com.hellodoc.healthcaresystem.user.home.model

data class ChatMessage(
    val message: String,
    val isUser: Boolean // true nếu là câu hỏi, false nếu là câu trả lời
)
