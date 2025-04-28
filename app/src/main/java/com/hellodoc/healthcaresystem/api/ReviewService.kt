package com.hellodoc.healthcaresystem.api

import com.hellodoc.healthcaresystem.requestmodel.ReviewRequest
import com.hellodoc.healthcaresystem.responsemodel.ReviewResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ReviewService {
    @POST("review")
    suspend fun createReview(@Body reviewRequest: ReviewRequest): Response<Any>

    @GET("review/doctor/{doctorId}")
    suspend fun getReviewsByDoctor(@Path("doctorId") doctorId: String): Response<List<ReviewResponse>>
}