package com.hellodoc.healthcaresystem.model.repository

import com.hellodoc.healthcaresystem.api.ReviewService
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.ReviewResponse
import com.hellodoc.healthcaresystem.requestmodel.ReviewRequest
import com.hellodoc.healthcaresystem.requestmodel.UpdateReviewRequest
import retrofit2.Response
import javax.inject.Inject

interface ReviewRepository {
    suspend fun getReviewsByDoctor(doctorId: String): Response<List<ReviewResponse>>
    suspend fun deleteReview(reviewId: String):Response<Unit>
    suspend fun createReview(reviewRequest: ReviewRequest):Response<Any>
    suspend fun updateReview(reviewId: String, updateRequest: UpdateReviewRequest):Response<ReviewResponse>

}
class ReviewRepositoryImpl @Inject constructor(
    private val reviewService: ReviewService
): ReviewRepository {
    override suspend fun getReviewsByDoctor(doctorId: String) = reviewService.getReviewsByDoctor(doctorId)
    override suspend fun deleteReview(reviewId: String) = reviewService.deleteReview(reviewId)
    override suspend fun createReview(reviewRequest: ReviewRequest) = reviewService.createReview(reviewRequest)
    override suspend fun updateReview(reviewId: String, updateRequest: UpdateReviewRequest) = reviewService.updateReview(
        reviewId, updateRequest
    )
}