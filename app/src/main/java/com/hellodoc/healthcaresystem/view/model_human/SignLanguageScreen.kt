//package com.yourapp.viewer
//
//import android.content.Context
//import android.view.Choreographer
//import android.view.Surface
//import android.view.SurfaceView
//import com.google.android.filament.*
//import com.google.android.filament.gltfio.AssetLoader
//import com.google.android.filament.gltfio.FilamentAsset
//import com.google.android.filament.gltfio.ResourceLoader
//import com.google.android.filament.utils.KTX1Loader
//import com.google.android.filament.utils.ModelViewer
//import java.nio.ByteBuffer
//
//class FilamentModelViewer(private val surfaceView: SurfaceView) {
//
//    private var choreographer: Choreographer
//    private lateinit var modelViewer: ModelViewer
//
//    private val frameScheduler = FrameCallback()
//
//    init {
//        choreographer = Choreographer.getInstance()
//    }
//
//    fun loadModel(context: Context, modelFileName: String) {
//        // Khởi tạo ModelViewer
//        modelViewer = ModelViewer(surfaceView)
//
//        // Đọc file GLB từ assets
//        val buffer = readAsset(context, modelFileName)
//
//        // Load model
//        modelViewer.loadModelGlb(buffer)
//        modelViewer.transformToUnitCube()
//
//        // Bắt đầu render loop
//        choreographer.postFrameCallback(frameScheduler)
//    }
//
//    private fun readAsset(context: Context, assetName: String): ByteBuffer {
//        val input = context.assets.open(assetName)
//        val bytes = ByteArray(input.available())
//        input.read(bytes)
//        input.close()
//
//        return ByteBuffer.allocateDirect(bytes.size).apply {
//            put(bytes)
//            rewind()
//        }
//    }
//
//    inner class FrameCallback : Choreographer.FrameCallback {
//        override fun doFrame(frameTimeNanos: Long) {
//            choreographer.postFrameCallback(this)
//            modelViewer.render(frameTimeNanos)
//        }
//    }
//
//    fun destroy() {
//        choreographer.removeFrameCallback(frameScheduler)
//    }
//}