package com.hellodoc.healthcaresystem.responsemodel

data class ChatMessage(
    val message: String,
    val isUser: Boolean // true nếu là câu hỏi, false nếu là câu trả lời
)
