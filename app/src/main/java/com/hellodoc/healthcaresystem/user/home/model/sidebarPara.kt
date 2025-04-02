package com.example.healthcaresystem.user.home.model

import android.graphics.drawable.Icon
import androidx.compose.ui.graphics.vector.ImageVector

data class SidebarItem(
    val nameField: String,
    val iconField: Int,
    val navigationField: String = ""
)
