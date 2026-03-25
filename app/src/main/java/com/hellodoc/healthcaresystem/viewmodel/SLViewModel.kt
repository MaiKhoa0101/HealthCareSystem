package com.hellodoc.healthcaresystem.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import com.hellodoc.healthcaresystem.interpreter.SignLanguageInterpreter

class SignLanguageViewModel(application: Application) : AndroidViewModel(application) {

    private val interpreter = SignLanguageInterpreter(application.applicationContext)

    private val _isCameraActive = MutableStateFlow(false)
    val isCameraActive = _isCameraActive.asStateFlow()

    private val _prediction = MutableStateFlow("Đang khởi tạo...")
    val prediction = _prediction.asStateFlow()

    // --- THÊM STATE CHO LANDMARKS ---
    private val _handResults = MutableStateFlow<HandLandmarkerResult?>(null)
    val handResults = _handResults.asStateFlow()

    private val _poseResults = MutableStateFlow<PoseLandmarkerResult?>(null)
    val poseResults = _poseResults.asStateFlow()

    private val _faceResults = MutableStateFlow<FaceLandmarkerResult?>(null)
    val faceResults = _faceResults.asStateFlow()

    init {
        interpreter.onResultListener = { text, confidence ->
            viewModelScope.launch {
                if (text == "...") {
                    _prediction.value = "Đang phân tích..."
                } else {
                    val formattedText = text.replace("_", " ").replaceFirstChar { it.uppercase() }
                    _prediction.value = "$formattedText (${(confidence * 100).toInt()}%)"
                }
            }
        }

        // --- LẮNG NGHE TỌA ĐỘ VÀ CẬP NHẬT UI ---
        interpreter.onLandmarksListener = { hands, poses, faces ->
            _handResults.value = hands
            _poseResults.value = poses
            _faceResults.value = faces
        }
    }

    fun toggleCamera() {
        if (_isCameraActive.value) {
            stopCamera()
        } else {
            startCamera()
        }
    }

    private fun startCamera() {
        Log.d("SLViewModel", "Bắt đầu mở Camera")
        _isCameraActive.value = true
        _prediction.value = "Đang tải mô hình PyTorch..."

        viewModelScope.launch {
            try {
                interpreter.initialize()
                _prediction.value = "Đang chờ hành động..."
                Log.d("SLViewModel", "Model đã load thành công")
            } catch (e: Exception) {
                Log.e("SLViewModel", "Lỗi load model: ${e.message}")
                _prediction.value = "Lỗi: Không tìm thấy file model!"
            }
        }
    }

    private fun stopCamera() {
        Log.d("SLViewModel", "Dừng Camera")
        _isCameraActive.value = false
        _prediction.value = "Đã dừng dịch"
        interpreter.close()
    }

    fun processCameraFrame(bitmap: Bitmap) {
        if (_isCameraActive.value) {
            interpreter.processFrame(bitmap)
        }
    }

    override fun onCleared() {
        super.onCleared()
        interpreter.close()
    }
}