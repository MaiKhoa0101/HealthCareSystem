package com.hellodoc.healthcaresystem.view.user.supportfunction

import android.content.Context
import android.util.Log
import com.google.android.filament.Engine
import io.github.sceneview.environment.Environment
import io.github.sceneview.loaders.EnvironmentLoader
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.model.ModelInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer

object SceneViewManager {
    private const val TAG = "SceneViewManager"

    private var engine: Engine? = null
    private var modelLoader: ModelLoader? = null
    private var environmentLoader: EnvironmentLoader? = null
    private var ericModelInstance: ModelInstance? = null
    private var globalEnvironment: Environment? = null

    private val _initializationState = MutableStateFlow(false)
    val initializationState: StateFlow<Boolean> = _initializationState.asStateFlow()


    @Volatile private var isInitializing = false
    suspend fun initialize(context: Context) {
        if (_initializationState.value || isInitializing) return
        if (engine != null && engine!!.isValid) {
            _initializationState.value = true
            return
        }

        isInitializing = true
        Log.d(TAG, "🚀 Initializing SceneView...")

        try {
            withContext(Dispatchers.Main) {
                loadNativeLibraries()
                if (engine == null) engine = Engine.create()
                engine?.let { eng ->
                    if (modelLoader == null) modelLoader = ModelLoader(eng, context)
                    if (environmentLoader == null) environmentLoader = EnvironmentLoader(eng, context)
                }
            }

            // Load Assets (IO Thread)
            val modelBuffer = withContext(Dispatchers.IO) {
                try {
                    context.assets.open("BoneEric.glb").use { inputStream ->
                        val bytes = inputStream.readBytes()
                        ByteBuffer.wrap(bytes)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error reading model", e)
                    null
                }
            }

            withContext(Dispatchers.Main) {
                if (modelBuffer != null && ericModelInstance == null) {
                    ericModelInstance = modelLoader?.createModelInstance(modelBuffer)
                    // ✅ FIX 1: Xả lệnh sau khi tạo Model (để bộ đệm ko bị đầy)
                    engine?.flushAndWait()
                }

                if (globalEnvironment == null) {
                    globalEnvironment = environmentLoader?.createHDREnvironment("environment.hdr")
                    // ✅ FIX 2: Xả lệnh sau khi tạo Environment (HDR cực nặng)
                    // Đây là nguyên nhân chính gây crash 8MB
                    engine?.flushAndWait()
                }

                if (engine != null && ericModelInstance != null && globalEnvironment != null) {
                    _initializationState.value = true
                    Log.d(TAG, "🎉 SceneView Ready - Buffer Flushed")
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "❌ Initialization failed", e)
        } finally {
            isInitializing = false
        }
    }

    private fun loadNativeLibraries() {
        try {
            System.loadLibrary("filament-jni")
            System.loadLibrary("gltfio-jni")
            System.loadLibrary("filament-utils-jni")
        } catch (e: UnsatisfiedLinkError) {
            Log.e(TAG, "Failed to load native libraries", e)
        }
    }

    fun getEngine(): Engine? = engine
    fun getModelInstance(): ModelInstance? = ericModelInstance
    fun getEnvironment(): Environment? = globalEnvironment

    fun blockUntilGPUCompletes() {
        try {
            Log.d(TAG, "🛑 Flushing Engine command buffer...")
            // flushAndWait chặn luồng hiện tại (Main Thread) cho đến khi GPU xử lý xong.
            // Điều này đảm bảo không còn lệnh vẽ nào trỏ đến Surface đã chết.
            engine?.flushAndWait()
        } catch (e: Exception) {
            Log.e(TAG, "Error flushing engine", e)
        }
    }
}