package com.hellodoc.healthcaresystem.view.model_human

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.google.android.filament.Engine
import io.github.sceneview.environment.Environment
import io.github.sceneview.loaders.EnvironmentLoader
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.model.ModelInstance

@Composable
fun Floating3DAssistant(
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    engine: Engine,
    modelInstance: ModelInstance?, // Nhận từ cha
    environment: Environment?      // Nhận từ cha
) {
    // Animation kích thước: Nhỏ (60dp) <-> Lớn (320dp)
    val size by animateDpAsState(
        targetValue = if (isExpanded) 150.dp else 50.dp,
        animationSpec = tween(durationMillis = 300), // Thời gian mượt mà
        label = "size"
    )

    Box(
        modifier = modifier
            .padding(bottom = 100.dp, end = 16.dp) // Cách lề
            .size(size) // Kích thước thay đổi theo animation
            .shadow(
                elevation = 10.dp,
                shape = CircleShape,
                clip = false
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = CircleShape
            )
            .clip(CircleShape) // Cắt toàn bộ nội dung thành hình tròn
            .clickable(enabled = !isExpanded) {
                // Chỉ cho bấm mở khi đang đóng.
                // Khi mở rồi thì phải bấm nút X để tắt (tránh bấm nhầm vào model)
                onExpandChange(true)
            }
    ) {
        // Nội dung bên trong
        if (isExpanded) {
            Box(modifier = Modifier.fillMaxSize()) {
                // --- TRẠNG THÁI MỞ RỘNG ---
                Box(
                    modifier = Modifier
                        .padding(3.dp)
                        .clip(CircleShape)
                        .fillMaxSize(),
                ) {
                    // 1. Màn hình 3D
                    SignLanguageAnimatableScreen(
                        engine = engine,
                        modelInstance = modelInstance,
                        environment = environment
                    )
                    // 2. Nút X (Đóng) - Góc trên trái
                    IconButton(
                        onClick = { onExpandChange(false) },
                        modifier = Modifier
                            .align(Alignment.TopStart) // Căn góc trên trái
                            .padding(top = 20.dp, start = 20.dp) // Thụt vào một chút cho đẹp
                            .size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        } else {
            // --- TRẠNG THÁI THU NHỎ (ICON) ---
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Face, // Hoặc icon Robot của bạn
                    contentDescription = "Open Assistant",
                    tint = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}