package com.hellodoc.healthcaresystem.model.repository
import com.google.android.filament.utils.transform
import com.hellodoc.healthcaresystem.model.roomDb.data.dao.AppSettingsDao
import com.hellodoc.healthcaresystem.model.roomDb.data.entity.AppSettingsEntity

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class SettingsRepository @Inject constructor (
    private val appSettingsDao: AppSettingsDao
) {
    // Lấy setting, nếu null (chưa có) thì trả về đối tượng mặc định
    val appSettings: Flow<AppSettingsEntity> = appSettingsDao.getSettings()
        .map { it ?: AppSettingsEntity() }

    // Hàm cập nhật trạng thái Dark Mode
    suspend fun setDarkMode(isDark: Boolean) {
        updateSettings { it.copy(isDarkMode = isDark) }
    }

    // Hàm cập nhật trạng thái Đã mở app lần đầu xong
    suspend fun setFirstLaunchCompleted() {
        updateSettings { it.copy(isFirstLaunch = false) }
    }

    // Hàm cập nhật trạng thái Đã tải data
    suspend fun setDataDownloaded(isDownloaded: Boolean) {
        updateSettings { it.copy(isDataDownloaded = isDownloaded) }
    }

    private suspend fun updateSettings(transform: (AppSettingsEntity) -> AppSettingsEntity) {
        var currentSettings = appSettingsDao.getSettings().firstOrNull()
        if (currentSettings != null) {
            currentSettings = transform(currentSettings)
        }
        appSettingsDao.insertOrUpdate(currentSettings!!)

    }

    suspend fun updateState(newState: AppSettingsEntity) {
        appSettingsDao.insertOrUpdate(newState)
    }
}