package com.hellodoc.healthcaresystem.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hellodoc.healthcaresystem.model.api.DetectionApiService
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.DetectionData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DetectionViewModel @Inject constructor(
    private val detectionApiService: DetectionApiService
) : ViewModel() {

    private val _detectionData = MutableStateFlow<DetectionData?>(null)
    val detectionData: StateFlow<DetectionData?> = _detectionData

    private val _isDetecting = MutableStateFlow(false)
    val isDetecting: StateFlow<Boolean> = _isDetecting

    fun detectJoints(imageFile: File) {
        viewModelScope.launch {
            _isDetecting.value = true
            try {
                val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)
                
                val response = detectionApiService.detectJoints(body)
                if (response.isSuccessful) {
                    _detectionData.value = response.body()?.data
                } else {
                    // Handle error
                }
            } catch (e: Exception) {
                // Handle exception
            } finally {
                _isDetecting.value = false
            }
        }
    }
}
