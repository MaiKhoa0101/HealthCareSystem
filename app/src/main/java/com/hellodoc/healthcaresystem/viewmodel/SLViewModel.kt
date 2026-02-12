package com.hellodoc.healthcaresystem.viewmodel

import SignLanguageInterpreter
import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log


class SignLanguageViewModel(application: Application) : AndroidViewModel(application) {

    // Khởi tạo class xử lý AI (đảm bảo bạn đã có class này từ các bước trước)
    private val interpreter = SignLanguageInterpreter(application.applicationContext)

    private val _isCameraActive = MutableStateFlow(false)
    val isCameraActive = _isCameraActive.asStateFlow()

    private val _prediction = MutableStateFlow("Đang khởi tạo...")
    val prediction = _prediction.asStateFlow()

    init {
        // GẮN LISTENER: Đây là bước quan trọng để nhận kết quả từ Interpreter về ViewModel
        interpreter.onResultListener = { text, confidence ->
            Log.d("SLViewModel", "Nhận kết quả: $text ($confidence)")
            viewModelScope.launch {
                _prediction.value = "$text (${(confidence * 100).toInt()}%)"
            }
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
        _prediction.value = "Đang tải mô hình AI..."

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