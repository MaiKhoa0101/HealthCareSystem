package com.hellodoc.healthcaresystem.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.hellodoc.healthcaresystem.requestmodel.ApplyDoctorRequest
import com.hellodoc.healthcaresystem.requestmodel.DoctorUiState
import com.hellodoc.healthcaresystem.requestmodel.ModifyClinic
import com.hellodoc.healthcaresystem.retrofit.RetrofitInstance
import com.hellodoc.healthcaresystem.responsemodel.GetDoctorResponse
import com.hellodoc.healthcaresystem.responsemodel.ServiceInput
import com.hellodoc.healthcaresystem.responsemodel.WorkHour
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.jetbrains.annotations.Async.Schedule
import java.io.File

class DoctorViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {
    private val _doctors = MutableStateFlow<List<GetDoctorResponse>>(emptyList())
    val doctors: StateFlow<List<GetDoctorResponse>> get() = _doctors

    private val _doctor = MutableStateFlow<GetDoctorResponse?>(null)
    val doctor: StateFlow<GetDoctorResponse?> get() = _doctor

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    fun fetchDoctors() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.doctor.getDoctors()
                if (response.isSuccessful) {
                    _doctors.value = response.body() ?: emptyList()
                    println("OK 1" + response.body())
                } else {
                    println("Lỗi API: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchDoctorById(doctorId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = RetrofitInstance.doctor.getDoctorById(doctorId)
                if (response.isSuccessful) {
                    _doctor.value = response.body()
                } else {
                    println("Lỗi lấy doctor theo id: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
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
            Log.e("PostViewModel", "Error preparing file part", e)
            null
        }
    }

    val loading = mutableStateOf(false)
    fun applyForDoctor(userId: String, request: ApplyDoctorRequest, context: Context) {
        loading.value = true

        viewModelScope.launch {
            try {
                val license = MultipartBody.Part.createFormData("license", request.license)
                val specialty = MultipartBody.Part.createFormData("specialty", request.specialty)
                val CCCD = MultipartBody.Part.createFormData("CCCD", request.CCCD)

                val licenseUrl = request.licenseUrl?.let {
                    prepareFilePart(context, it, "licenseUrl")
                }
                val faceUrl = request.faceUrl?.let {
                    prepareFilePart(context, it, "faceUrl")
                }
                val avatarURL = request.avatarURL?.let {
                    prepareFilePart(context, it, "avatarURL")
                }
                val frontCccdUrl = request.frontCccdUrl?.let {
                    prepareFilePart(context, it, "frontCccdUrl")
                }
                val backCccdUrl = request.backCccdUrl?.let {
                    prepareFilePart(context, it, "backCccdUrl")
                }

                val response = RetrofitInstance.doctor.applyForDoctor(
                    userId,
                    license,
                    specialty,
                    CCCD,
                    licenseUrl,
                    faceUrl,
                    avatarURL,
                    frontCccdUrl,
                    backCccdUrl
                )

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            context,
                            "Đăng ký thành công. Vui lòng chờ xác thực.",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.d("Đăng ký Bsi", "Đăng ký bác sĩ thành công")
                    } else {
                        Toast.makeText(
                            context,
                            "Đăng ký bác sĩ thất bại: ${response.errorBody()?.string()}",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e(
                            "Đăng ký Bsi",
                            "Đăng ký bác sĩ thất bại: ${response.errorBody()?.string()}"
                        )
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Đăng ký bác sĩ thất bại: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e("Đăng ký Bsi", "Đăng ký bác sĩ thất bại", e)
                }
                Log.e("Đăng ký Bsi", "Đăng ký bác sĩ thất bại ", e)
            } finally {
                loading.value = false
            }
        }
    }

    fun updateClinic(clinicUpdateData: ModifyClinic, doctorId: String, context: Context) {
        viewModelScope.launch {
            try {
                val gson = Gson()

                val addressPart = MultipartBody.Part.createFormData("address", clinicUpdateData.address)
                val workHourJson = gson.toJson(clinicUpdateData.workingHours)
                val workHourPart = MultipartBody.Part.createFormData("workingHour", workHourJson)

                val servicesJsonList = clinicUpdateData.services.map {
                    ServiceInput(
                        specializationName = it.specializationName,
                        priceFrom = it.priceFrom,
                        priceTo = it.priceTo,
                        description = it.description,
                        imageUri = Uri.EMPTY // tạm để imageUri rỗng khi serialize
                    )
                }
                val servicesJson = gson.toJson(servicesJsonList)
                val servicesPart = MultipartBody.Part.createFormData("services", servicesJson)

                // Upload từng ảnh
                val imageParts = clinicUpdateData.services.mapIndexed { index, service ->
                    prepareFilePart(context, service.imageUri, "image$index")
                }
                val updatedImageParts: List<MultipartBody.Part> = imageParts.filterNotNull()
                val response = RetrofitInstance.doctor.updateClinic(
                    doctorId,
                    addressPart,
                    workHourPart,
                    servicesPart,
                    updatedImageParts
                )

                println(response.toString())
                println(response.body())

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Thay đổi thất bại: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e("Thay đổi clinic", "Thất bại", e)
                }
                Log.e("Thay đổi Clinic", "Thất bại ", e)
            }
        }
    }
}

