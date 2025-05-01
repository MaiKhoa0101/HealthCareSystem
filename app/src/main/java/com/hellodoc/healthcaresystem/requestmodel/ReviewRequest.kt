package com.hellodoc.healthcaresystem.requestmodel

data class ReviewRequest(
    val userId: String,
    val doctorId: String,
    val rating: Int,
    val comment: String
)
data class UpdateReviewRequest(
    val rating: Int,
    val comment: String
)

