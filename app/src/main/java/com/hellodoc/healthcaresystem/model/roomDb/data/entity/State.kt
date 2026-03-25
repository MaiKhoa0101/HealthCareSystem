package com.hellodoc.healthcaresystem.model.roomDb.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val id: Int = 0, // Luôn cố định là 0 để chỉ có 1 row cài đặt
    val isDarkMode: Boolean = false,     // Mặc định là Light Mode
    val isFirstLaunch: Boolean = true,   // Mặc định là Lần đầu
    val isDataDownloaded: Boolean = false // Mặc định là Chưa tải
)