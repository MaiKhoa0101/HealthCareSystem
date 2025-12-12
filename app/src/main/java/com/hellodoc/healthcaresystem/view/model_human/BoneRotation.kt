//package com.hellodoc.healthcaresystem.view.model_human
//
//import android.view.SurfaceView
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.viewinterop.AndroidView
//import com.yourapp.viewer.FilamentModelViewer
//
//@Composable
//fun GLBModelViewer(
//    modelFileName: String = "eric.glb"
//) {
//    val context = LocalContext.current
//    var modelViewer: FilamentModelViewer? by remember { mutableStateOf(null) }
//
//    DisposableEffect(Unit) {
//        onDispose {
//            modelViewer?.destroy()
//        }
//    }
//
//    AndroidView(
//        modifier = Modifier.fillMaxSize(),
//        factory = { ctx ->
//            SurfaceView(ctx).apply {
//                val viewer = FilamentModelViewer(this)
//                viewer.loadModel(context, modelFileName)
//                modelViewer = viewer
//            }
//        }
//    )
//}
//
//
