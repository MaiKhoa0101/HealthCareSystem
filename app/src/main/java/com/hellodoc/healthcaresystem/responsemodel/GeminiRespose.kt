package com.hellodoc.healthcaresystem.responsemodel

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