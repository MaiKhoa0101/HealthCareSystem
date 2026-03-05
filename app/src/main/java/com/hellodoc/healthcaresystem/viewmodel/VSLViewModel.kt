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
//
//            // 1. Tạo một List chứa các object VSL giả (Mock Data)
//            val mockData = listOf(
//                VSL(
//                    url = "http://qipedc.moet.gov.vn/videos/W04056B.mp4", // Chúc bạn Rickroll vui vẻ nhé =))
//                    gross = "tôi yêu em"
//                )
//            )
//
//            // 2. Dùng Response.success() để bọc List đó lại thành 1 cục Response chuẩn của Retrofit
//            val response = Response.success(mockData)

            if (response.isSuccessful) {
                _vslResponse.value = response.body() ?: emptyList()
            } else {
                Log.e("VSLViewModel", "Lỗi API: ${response.errorBody()?.string()}")
                _vslResponse.value = emptyList()
            }
        }
    }

}
