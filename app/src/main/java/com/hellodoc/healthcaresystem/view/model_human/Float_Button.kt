package com.hellodoc.healthcaresystem.view.model_human

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.google.android.filament.Engine
import io.github.sceneview.environment.Environment
import io.github.sceneview.model.ModelInstance

/**
 * ✅ CRASH-PROOF 3D ASSISTANT
 *
 * Cải tiến:
 * - Tự động đóng khi resources bị null
 * - Không render khi thiếu dependencies
 * - Safe cleanup sequence
 */
@Composable
fun Floating3DAssistant(
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    engine: Engine?,
    modelInstance: ModelInstance?,
    environment: Environment?
) {
    // ===== AUTO-CLOSE KHI MẤT RESOURCES =====
    LaunchedEffect(engine, modelInstance, environment) {
        // Nếu đang mở nhưng thiếu resources → Tự động đóng
        if (isExpanded && (engine == null || modelInstance == null || environment == null)) {
            android.util.Log.d("Floating3D", "⚠️ Resources null, auto-closing assistant")
            onExpandChange(false)
        }
    }

    // Animation kích thước
    val size by animateDpAsState(
        targetValue = if (isExpanded) 200.dp else 50.dp,
        animationSpec = tween(durationMillis = 300),
        label = "size"
    )

    Box(
        modifier = modifier
            .padding(bottom = 100.dp, end = 16.dp)
            .size(size)
            .shadow(
                elevation = 10.dp,
                shape = CircleShape,
                clip = false
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = CircleShape
            )
            .clip(CircleShape)
            .clickable(enabled = !isExpanded) {
                // CHỈ MỞ KHI CÓ ĐỦ RESOURCES
                if (engine != null && modelInstance != null && environment != null) {
                    onExpandChange(true)
                } else {
                    android.util.Log.w("Floating3D", "Cannot open: Resources not ready")
                }
            }
    ) {
        if (isExpanded) {
            // ===== KIỂM TRA NULL TRƯỚC KHI RENDER =====
            if (engine != null && modelInstance != null && environment != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(3.dp)
                        .clip(CircleShape)
                ) {
                    // Render 3D scene
                    SignLanguageAnimatableScreen(
                        engine = engine,
                        modelInstance = modelInstance,
                        environment = environment
                    )

                    // Overlay để đóng
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                onExpandChange(false)
                            }
                    )
                }
            } else {
                // Fallback: Nếu thiếu resources → Hiện icon
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = "Loading...",
                        tint = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        } else {
            // ===== TRẠNG THÁI THU NHỎ =====
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Face,
                    contentDescription = "Open Assistant",
                    tint = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}