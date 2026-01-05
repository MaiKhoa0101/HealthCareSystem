package com.hellodoc.healthcaresystem.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hellodoc.healthcaresystem.model.repository.SettingsRepository
import com.hellodoc.healthcaresystem.model.roomDb.data.entity.AppSettingsEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject // Hoặc javax.inject.Inject tuỳ version Hilt bạn dùng
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn // <--- QUAN TRỌNG: Thêm dòng này
import kotlinx.coroutines.launch

@HiltViewModel
class StateViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    // StateFlow chứa toàn bộ setting, UI sẽ lắng nghe cái này
    val appSettings: StateFlow<AppSettingsEntity> = settingsRepository.appSettings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettingsEntity()
        )

    fun toggleDarkMode() {
        // Vì appSettings là StateFlow nên truy cập .value là an toàn và đồng bộ
        val current = appSettings.value
        viewModelScope.launch {
            settingsRepository.updateState(current.copy(isDarkMode = !current.isDarkMode))
        }
    }

    fun completeOnboarding() {
        val current = appSettings.value
        viewModelScope.launch {
            settingsRepository.updateState(current.copy(isFirstLaunch = false))
        }
    }

    fun setDownloadStatus(downloaded: Boolean) {
        val current = appSettings.value
        viewModelScope.launch {
            settingsRepository.updateState(current.copy(isDataDownloaded = downloaded))
        }
    }
}