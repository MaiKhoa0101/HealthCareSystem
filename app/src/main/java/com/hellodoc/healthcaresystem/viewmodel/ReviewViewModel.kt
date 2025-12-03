package com.hellodoc.healthcaresystem.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.ReviewResponse
import com.hellodoc.healthcaresystem.model.repository.ReviewRepository
import com.hellodoc.healthcaresystem.model.retrofit.RetrofitInstance
import com.hellodoc.healthcaresystem.requestmodel.ReviewRequest
import com.hellodoc.healthcaresystem.requestmodel.UpdateReviewRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _reviews = MutableStateFlow<List<ReviewResponse>>(emptyList())
    val reviews: StateFlow<List<ReviewResponse>> get() = _reviews

    fun getReviewsByDoctor(doctorId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = reviewRepository.getReviewsByDoctor(doctorId)
                if (response.isSuccessful) {
                    _reviews.value = response.body()?.sortedByDescending {
                        it.createdAt
                    } ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteReview(
        reviewId: String,
        onDeleteClick: (String) -> Unit
    ){
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = reviewRepository.deleteReview(reviewId)
                if (response.isSuccessful) {
                    onDeleteClick(reviewId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun handleReviewSubmit(
        reviewId: String?,
        userId: String,
        doctorId: String,
        rating: Int,
        comment: String,
        onSubmitClick: (Int, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = if (reviewId == null) {
                    val reviewRequest = ReviewRequest(userId, doctorId, rating, comment)
                    reviewRepository.createReview(reviewRequest)
                } else {
                    val updateRequest = UpdateReviewRequest(rating, comment)
                    reviewRepository.updateReview(reviewId, updateRequest)
                }

                onSubmitClick(rating, comment)
            } catch (e: Exception) {
                e.printStackTrace()
                onSubmitClick(rating, comment)
            }
        }
    }
}
