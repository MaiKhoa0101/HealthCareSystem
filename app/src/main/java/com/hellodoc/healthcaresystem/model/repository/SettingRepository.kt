package com.hellodoc.healthcaresystem.model.repository
import com.google.android.filament.utils.transform
import com.hellodoc.healthcaresystem.model.roomDb.data.dao.AppSettingsDao
import com.hellodoc.healthcaresystem.model.roomDb.data.entity.AppSettingsEntity

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject


interface SettingsRepository{
    val appSettings: Flow<AppSettingsEntity>
    suspend fun setDarkMode(
        isDark: Boolean
    )
    suspend fun setFirstLaunchCompleted()
    suspend fun setDataDownloaded(
        isDownloaded: Boolean
    )
    suspend fun updateState(
        newState: AppSettingsEntity
    )
    suspend fun getStateDownload(): Boolean
    suspend fun getThemeState(): Boolean
    suspend fun updateSettings(
        transform: (AppSettingsEntity) -> AppSettingsEntity
    )
}

class SettingsRepositoryImpl @Inject constructor (
    private val appSettingsDao: AppSettingsDao
): SettingsRepository {
    // Lấy setting, nếu null (chưa có) thì trả về đối tượng mặc định
    override val appSettings: Flow<AppSettingsEntity> = appSettingsDao.getSettings()
        .map { it ?: AppSettingsEntity() }

    // Hàm cập nhật trạng thái Dark Mode
    override suspend fun setDarkMode(isDark: Boolean) {
        updateSettings { it.copy(isDarkMode = isDark) }
    }

    // Hàm cập nhật trạng thái Đã mở app lần đầu xong
    override suspend fun setFirstLaunchCompleted() {
        updateSettings { it.copy(isFirstLaunch = false) }
    }

    // Hàm cập nhật trạng thái Đã tải data
    override suspend fun setDataDownloaded(isDownloaded: Boolean) {
        println("Gọi được set data downloaded")
        updateSettings { it.copy(isDataDownloaded = isDownloaded) }
    }

    override suspend fun updateSettings(transform: (AppSettingsEntity) -> AppSettingsEntity) {
        // 1. Lấy setting hiện tại. Nếu null (chưa có trong DB) thì tạo object mặc định
        // Toán tử ?: (Elvis operator) sẽ cứu bạn ở đây
        val currentSettings = appSettingsDao.getSettings().firstOrNull() ?: AppSettingsEntity()

        // 2. Áp dụng thay đổi
        val updatedSettings = transform(currentSettings)

        // 3. Lưu vào DB (Không còn !! nữa vì updatedSettings chắc chắn không null)
        appSettingsDao.insertOrUpdate(updatedSettings)
    }

     override suspend fun updateState(newState: AppSettingsEntity) {
        appSettingsDao.insertOrUpdate(newState)
    }

    override suspend fun getStateDownload(): Boolean{
        return appSettingsDao.getStateDownload()
    }
    override suspend fun getThemeState():Boolean {
        return appSettingsDao.getThemeState()
    }
}