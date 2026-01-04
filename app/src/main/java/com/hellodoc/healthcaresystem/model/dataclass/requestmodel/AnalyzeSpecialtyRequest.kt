package com.hellodoc.healthcaresystem.model.dataclass.requestmodel

data class AnalyzeSpecialtyRequest(
    val text: String,
    val specialties: List<String>
)
