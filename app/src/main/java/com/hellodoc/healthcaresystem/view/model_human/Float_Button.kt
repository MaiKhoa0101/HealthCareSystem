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
    environment: Environment?,
    videoUrl: String
) {
    // ===== AUTO-CLOSE KHI MẤT RESOURCES =====
    LaunchedEffect(engine, environment) {
        // Nếu đang mở nhưng Engine hoặc Environment hỏng → Tự động đóng
        // Note: modelInstance có thể null tạm thời khi đang loading
        val isCoreResourcesInvalid = engine == null || !engine.isValid || environment == null
        
        if (isExpanded && isCoreResourcesInvalid) {
            android.util.Log.w("Floating3D", "⚠️ Core resources invalid, auto-closing assistant")
            onExpandChange(false)
        }
    }
    
    // ===== FLUSH GPU WHEN CLOSING =====
    LaunchedEffect(isExpanded) {
        if (!isExpanded && engine?.isValid == true) {
            // When closing, flush GPU to free command buffer
            try {
                engine.flushAndWait()
                android.util.Log.d("Floating3D", "🧹 GPU flushed on close")
            } catch (e: Exception) {
                android.util.Log.e("Floating3D", "❌ Error flushing on close", e)
            }
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
                // CHỈ MỞ KHI ENGINE VÀ ENVIRONMENT HỢP LỆ
                // ModelInstance sẽ được VideoPlayer tạo khi thấy isExpanded = true
                val canStartOpening = engine != null && engine.isValid && environment != null
                             
                if (canStartOpening) {
                    onExpandChange(true)
                } else {
                    android.util.Log.w("Floating3D", "Cannot open: Core resources not ready")
                }
            }
    ) {
        if (isExpanded) {
            // ===== KIỂM TRA NULL VÀ VALIDITY TRƯỚC KHI RENDER =====
            val canRender = engine != null && 
                           engine.isValid && 
                           modelInstance != null && 
                           environment != null
                           
            if (canRender) {
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
                        environment = environment,
                        videoUrl = videoUrl
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