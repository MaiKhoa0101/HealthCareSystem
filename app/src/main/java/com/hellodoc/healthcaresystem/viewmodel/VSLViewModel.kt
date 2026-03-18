package com.hellodoc.healthcaresystem.viewmodel

import android.util.Log
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.VSL
import com.hellodoc.healthcaresystem.model.repository.VSLRepository
import kotlinx.coroutines.flow.MutableStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hellodoc.healthcaresystem.model.dataclass.requestmodel.Subtitle
import com.hellodoc.healthcaresystem.view.user.supportfunction.TranslationManager
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Response

@HiltViewModel
class VSLViewModel @Inject constructor(
    private val vslRepository: VSLRepository
): ViewModel() {

    private val _vslResponse = MutableStateFlow<List<VSL>> (emptyList ());
    val vslResponse: StateFlow<List<VSL>> = _vslResponse;


    fun fetchVSL(subtitle: Subtitle) {
        viewModelScope.launch {
            println("fetchVSL: $subtitle")

            // Tạm thời ẩn phần gọi API thật
             val response = vslRepository.getSignLanguageVideoPlaylist(subtitle)

            if (response.isSuccessful) {
                _vslResponse.value = response.body()?.playlist ?: emptyList()
            } else {
                Log.e("VSLViewModel", "Lỗi API: ${response.errorBody()?.string()}")
                _vslResponse.value = emptyList()
            }
        }
    }


    private val translationManager = TranslationManager()
    private val _translatedText = MutableStateFlow<String?>(null)
    val translatedText: StateFlow<String?> = _translatedText

    init {
        // Tải model ngay khi ViewModel khởi tạo
        viewModelScope.launch {
            translationManager.initialize()
        }
    }

    fun translateViToEn(text: String) {
        viewModelScope.launch {
            try {
                val result = translationManager.translateViToEn(text)
                _translatedText.value = result
            } catch (e: Exception) {
                Log.e("VSLViewModel", "Translation error: ${e.message}")
            }
        }
    }

    fun translateEnToVi(text: String) {
        viewModelScope.launch {
            try {
                val result = translationManager.translateEnToVi(text)
                _translatedText.value = result
            } catch (e: Exception) {
                Log.e("VSLViewModel", "Translation error: ${e.message}")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        translationManager.release()
    }

}
