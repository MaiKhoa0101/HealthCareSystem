package com.hellodoc.healthcaresystem.model.repository

import com.hellodoc.healthcaresystem.model.api.VSLService
import com.hellodoc.healthcaresystem.model.dataclass.requestmodel.Subtitle
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.ResponseVSLVideo
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.VSL
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

interface VSLRepository{
    suspend fun getSignLanguageVideoPlaylist(subtitle: Subtitle): Response<ResponseVSLVideo>
}

class VSLRepositoryImpl @Inject constructor(
    private val vslService: VSLService
): VSLRepository{
    override suspend fun getSignLanguageVideoPlaylist(subtitle: Subtitle): Response<ResponseVSLVideo> {
        return vslService.getSignLanguageVideoPlaylist(subtitle)
    }
}