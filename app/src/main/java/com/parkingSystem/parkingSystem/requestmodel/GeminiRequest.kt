package com.parkingSystem.parkingSystem.requestmodel

data class GeminiRequest(
    val contents: List<Content>
)

data class Content(
    val parts: List<Part>
)


data class InlineData(
    val mime_type: String,
    val data: String
)
data class Part(
    val text: String? = null,
    val inline_data: InlineData? = null
)