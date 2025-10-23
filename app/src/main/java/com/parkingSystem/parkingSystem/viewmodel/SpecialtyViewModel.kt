package com.parkingSystem.parkingSystem.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parkingSystem.parkingSystem.requestmodel.SpecialtyRequest
import com.parkingSystem.parkingSystem.responsemodel.Doctor
import com.parkingSystem.parkingSystem.responsemodel.GetSpecialtyResponse
import com.parkingSystem.parkingSystem.retrofit.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class SpecialtyViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {
    private val _specialties = MutableStateFlow<List<GetSpecialtyResponse>>(emptyList())
    val specialties: StateFlow<List<GetSpecialtyResponse>> get() = _specialties

    fun fetchSpecialties(forceRefresh: Boolean = false) {
        // Tránh gọi lại nếu đã có dữ liệu và không yêu cầu refresh
        if(_specialties.value.isNotEmpty() &&forceRefresh) return

        viewModelScope.launch {
            try {
                val response = RetrofitInstance.specialtyService.getSpecialties()
                if (response.isSuccessful) {
                    _specialties.value = response.body() ?: emptyList()
                    //println("OK 1" + response.body())
                } else {
                    println("Lỗi API: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    // StateFlow for doctors
    private val _doctors = MutableStateFlow<List<Doctor>>(emptyList())
    val doctors: StateFlow<List<Doctor>> get() = _doctors

    // Optional: StateFlow for the specialty info
    private val _specialty = MutableStateFlow<GetSpecialtyResponse?>(null)
    val specialty: StateFlow<GetSpecialtyResponse?> get() = _specialty

    // Danh sách bác sĩ sau khi lọc
    private val _filteredDoctors = MutableStateFlow<List<Doctor>>(emptyList())
    val filteredDoctors: StateFlow<List<Doctor>> get() = _filteredDoctors

    // Lọc theo địa chỉ
    fun filterDoctorsByLocation(location: String) {
        _filteredDoctors.value = _doctors.value.filter {
            it.address?.contains(location, ignoreCase = true) == true
        }
    }

    // Xóa bộ lọc (hiển thị tất cả)
    fun clearFilter() {
        _filteredDoctors.value = _doctors.value
    }

    fun fetchSpecialtyDoctor(specialtyID: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.specialtyService.getSpecialtyById(specialtyID)
                if (response.isSuccessful) {
                    val specialtyResponse = response.body()
                    if (specialtyResponse != null) {
                        _specialty.value = specialtyResponse
                        _doctors.value = specialtyResponse.doctors
                        _filteredDoctors.value = specialtyResponse.doctors
                        //println("OK: Successfully retrieved ${specialtyResponse.doctors.size} doctors")
                    } else {
                        _doctors.value = emptyList()
                        println("API returned null response body")
                    }
                } else {
                    println("API Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                println("Exception: ${e.message}")
                e.printStackTrace()
            }
        }
    }


    private fun prepareFilePart(
        context: Context,
        fileUri: Uri,
        partName: String
    ): MultipartBody.Part? {
        return try {
            val inputStream = context.contentResolver.openInputStream(fileUri)
            val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
            tempFile.outputStream().use { outputStream ->
                inputStream?.copyTo(outputStream)
            }

            val requestFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData(partName, tempFile.name, requestFile)
        } catch (e: Exception) {
            Log.e("DoctorViewModel", "Error preparing file part", e)
            null
        }
    }

    private val _createSpecialtyMessage  = MutableStateFlow<GetSpecialtyResponse?>(null)
    val createSpecialtyMessage : StateFlow<GetSpecialtyResponse?> = _createSpecialtyMessage

    fun createSpecialty(data: SpecialtyRequest, context: Context) {
        viewModelScope.launch {
            try {
                val name = MultipartBody.Part.createFormData("name", data.name)
                val description = MultipartBody.Part.createFormData("description", data.description)

                val icon = data.icon?.let {
                    prepareFilePart(context, it, "icon")
                }

                val response = icon?.let {
                    RetrofitInstance.specialtyService.createSpecialty(
                        name,
                        it,
                        description
                    )
                }
                if (response != null) {
                    if(response.isSuccessful){
                        _createSpecialtyMessage.value = response.body()
                        fetchSpecialties()
                        Toast.makeText(
                            context,
                            "Tạo chuyên khoa thành công.",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.d("Tạo chuyên khoa", "thành công")
                    } else {
                        Toast.makeText(
                            context,
                            "Tạo chuyên khoa thất bại, Khoa đã tồn tại.",
                            Toast.LENGTH_LONG
                        ).show()
                        println("Lỗi API: ${response.errorBody()?.string()}")
                    }
                }

            }catch (e: Exception) {
                Toast.makeText(
                    context,
                    "Tạo chuyên khoa thất bại: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                e.printStackTrace()
            }
        }
    }
}