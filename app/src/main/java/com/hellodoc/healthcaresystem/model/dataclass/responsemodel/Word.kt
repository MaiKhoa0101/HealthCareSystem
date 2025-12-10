package com.hellodoc.healthcaresystem.model.dataclass.responsemodel

data class Word(
    val suggestion: String,
    val score: Int,
    val label: List<String>
)

data class WordResponse(
    val word: String,
    val success :Boolean,
    val nodes: List<Word>

)

data class WordRequest(
    val word: String
)