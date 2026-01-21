package com.hellodoc.healthcaresystem.model.repository

import com.hellodoc.healthcaresystem.model.api.SubtitleService
import com.hellodoc.healthcaresystem.model.dataclass.requestmodel.SubtitleRequest
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.SubtitleResponse
import jakarta.inject.Inject
import retrofit2.Response

interface SubtitleRepository {
    suspend fun getSubtitle(videoUrl: String): Response<SubtitleResponse>
}
class SubtitleRepositoryImpl @Inject constructor(
    private val subtitleService: SubtitleService
): SubtitleRepository {
    override suspend fun getSubtitle(videoUrl: String) = subtitleService.getSubtitle(SubtitleRequest(videoUrl = videoUrl))

}