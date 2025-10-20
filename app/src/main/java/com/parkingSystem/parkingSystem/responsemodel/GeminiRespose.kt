package com.parkingSystem.parkingSystem.responsemodel

data class GeminiResponse(
    val candidates: List<Candidate>
)

data class Candidate(
    val content: AnswerContent
)

data class AnswerContent(
    val parts: List<AnswerPart>
)

data class AnswerPart(
    val text: String
)

data class GeminiFullResponse(
    val answer: GeminiResponse,
    val relatedPosts: List<PostResponse>  // Bài viết liên quan từ hệ thống
)
