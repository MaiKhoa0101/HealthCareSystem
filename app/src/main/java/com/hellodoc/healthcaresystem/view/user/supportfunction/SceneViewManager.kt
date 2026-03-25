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

/**
 * ✅ FIXED: Support multiple ModelInstances (one per video)
 * 
 * Previous issue: All videos shared 1 ModelInstance → crash when navigating between videos
 * Solution: Cache model buffer, create new ModelInstance for each video
 */
object SceneViewManager {
    private const val TAG = "SceneViewManager"

    private var engine: Engine? = null
    private var modelLoader: ModelLoader? = null
    private var environmentLoader: EnvironmentLoader? = null
    
    // ===== CHANGED: Cache model buffer instead of ModelInstance =====
    private var cachedModelBuffer: ByteBuffer? = null
    
    private var globalEnvironment: Environment? = null

    private val _initializationState = MutableStateFlow(false)
    val initializationState: StateFlow<Boolean> = _initializationState.asStateFlow()

    /**
     * Check if core resources (Engine, Environment, Model buffer) are ready
     */
    fun isResourcesValid(): Boolean {
        return try {
            engine?.isValid == true && 
            cachedModelBuffer != null && 
            globalEnvironment != null
        } catch (e: Exception) {
            Log.e(TAG, "Error checking resource validity", e)
            false
        }
    }


    @Volatile private var isInitializing = false
    suspend fun initialize(context: Context) {
        if (_initializationState.value || isInitializing) {
            Log.d(TAG, "Already initialized or initializing, skipping...")
            return
        }
        if (engine != null && engine!!.isValid) {
            _initializationState.value = true
            Log.d(TAG, "Engine already valid, skipping initialization")
            return
        }

        isInitializing = true
        Log.d(TAG, "🚀 Initializing SceneView...")

        try {
            withContext(Dispatchers.Main) {
                loadNativeLibraries()
                
                // Create Engine with default config
                if (engine == null) {
                    engine = Engine.create()
                    Log.d(TAG, "✅ Engine created")
                }
                
                engine?.let { eng ->
                    if (modelLoader == null) modelLoader = ModelLoader(eng, context)
                    if (environmentLoader == null) environmentLoader = EnvironmentLoader(eng, context)
                }
            }

            // Load model buffer (IO Thread) - Cache for reuse
                cachedModelBuffer = withContext(Dispatchers.IO) {
                    try {
                        context.assets.open("BoneEric.glb").use { inputStream ->
                            val bytes = inputStream.readBytes()
                            // ===== FIX: USE DIRECT BYTEBUFFER =====
                            // Native code (Filament) needs a stable memory address.
                            // Standard ByteBuffers (wrap) can be moved by GC, causing SIGSEGV.
                            val directBuffer = ByteBuffer.allocateDirect(bytes.size)
                            directBuffer.put(bytes)
                            directBuffer.flip()
                            Log.d(TAG, "📦 Model buffer loaded into direct memory (${bytes.size} bytes)")
                            directBuffer
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error reading model", e)
                        null
                    }
                }

            withContext(Dispatchers.Main) {
                // Load environment (shared across all videos - safe to share)
                if (globalEnvironment == null) {
                    globalEnvironment = environmentLoader?.createHDREnvironment("environment.hdr")
                    engine?.flushAndWait()
                    Log.d(TAG, "✅ Environment loaded")
                }

                if (engine != null && cachedModelBuffer != null && globalEnvironment != null) {
                    _initializationState.value = true
                    Log.d(TAG, "🎉 SceneView Ready")
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
            Log.d(TAG, "Native libraries loaded successfully")
        } catch (e: UnsatisfiedLinkError) {
            Log.e(TAG, "Failed to load native libraries", e)
        }
    }

    fun getEngine(): Engine? {
        return if (engine?.isValid == true) engine else null
    }
    
    /**
     * ✅ NEW: Create a new ModelInstance for each video
     * This prevents cross-video conflicts
     */
    fun createModelInstance(): ModelInstance? {
        return try {
            if (engine?.isValid == true && cachedModelBuffer != null && modelLoader != null) {
                val instance = modelLoader!!.createModelInstance(cachedModelBuffer!!)
                engine?.flushAndWait()
                Log.d(TAG, "✅ Created new ModelInstance")
                instance
            } else {
                Log.w(TAG, "⚠️ Cannot create ModelInstance: resources not ready")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error creating ModelInstance", e)
            null
        }
    }
    
    fun getEnvironment(): Environment? {
        return if (engine?.isValid == true) globalEnvironment else null
    }

    fun blockUntilGPUCompletes() {
        try {
            if (engine?.isValid == true) {
                Log.d(TAG, "🛑 Flushing Engine command buffer...")
                engine?.flushAndWait()
                Log.d(TAG, "✅ Engine flush completed")
            } else {
                Log.w(TAG, "⚠️ Engine invalid, skipping flush")
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error flushing engine", e)
        }
    }
}