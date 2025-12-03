package com.hellodoc.healthcaresystem.model.repository

import com.hellodoc.healthcaresystem.api.ReviewService
import com.hellodoc.healthcaresystem.requestmodel.ReviewRequest
import com.hellodoc.healthcaresystem.requestmodel.UpdateReviewRequest
import javax.inject.Inject

class ReviewRepository @Inject constructor(
    private val reviewService: ReviewService
) {
    suspend fun getReviewsByDoctor(doctorId: String) = reviewService.getReviewsByDoctor(doctorId)
    suspend fun deleteReview(reviewId: String) = reviewService.deleteReview(reviewId)
    suspend fun createReview(reviewRequest: ReviewRequest) = reviewService.createReview(reviewRequest)
    suspend fun updateReview(reviewId: String, updateRequest: UpdateReviewRequest) = reviewService
}