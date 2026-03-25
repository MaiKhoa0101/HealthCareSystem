package com.hellodoc.healthcaresystem.model.repository

import android.content.SharedPreferences
import com.hellodoc.healthcaresystem.api.GeminiService
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.GeminiResponse
import com.hellodoc.healthcaresystem.requestmodel.GeminiRequest
import jakarta.inject.Inject
import retrofit2.Response

interface GeminiRepository{
    suspend fun askGemini(
        apiKey: String,
        request: GeminiRequest
    ): Response<GeminiResponse>

}
class GeminiRepositoryImpl @Inject constructor(
    private val geminiService: GeminiService
): GeminiRepository {
    override suspend fun askGemini(
        apiKey: String,
        request: GeminiRequest
    ) = geminiService.askGemini(apiKey, request)
}