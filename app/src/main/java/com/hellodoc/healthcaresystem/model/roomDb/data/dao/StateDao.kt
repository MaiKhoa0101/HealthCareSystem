package com.hellodoc.healthcaresystem.model.roomDb.data.dao
import androidx.room.*
import com.hellodoc.healthcaresystem.model.roomDb.data.entity.AppSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppSettingsDao {
    // Lấy cài đặt (trả về Flow để UI tự đổi màu khi setting thay đổi)
    @Query("SELECT * FROM app_settings WHERE id = 0")
    fun getSettings(): Flow<AppSettingsEntity?>

    // Lưu hoặc Cập nhật cài đặt
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(settings: AppSettingsEntity)

    @Query("SELECT isDataDownloaded FROM app_settings WHERE id = 0")
    suspend fun getStateDownload(): Boolean

    @Query("SELECT isDarkMode FROM app_settings WHERE id = 0")
    suspend fun getThemeState(): Boolean


}