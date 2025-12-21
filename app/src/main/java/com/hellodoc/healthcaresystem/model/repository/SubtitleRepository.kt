package com.hellodoc.healthcaresystem.model.repository

import com.hellodoc.healthcaresystem.model.api.SubtitleService
import com.hellodoc.healthcaresystem.model.dataclass.requestmodel.SubtitleRequest
import jakarta.inject.Inject

class SubtitleRepository@Inject constructor(
    private val subtitleService: SubtitleService
){
    suspend fun getSubtitle(videoUrl: String) = subtitleService.getSubtitle(SubtitleRequest(videoUrl = videoUrl))

}