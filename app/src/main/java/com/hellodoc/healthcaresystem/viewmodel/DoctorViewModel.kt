package com.hellodoc.healthcaresystem.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.hellodoc.healthcaresystem.requestmodel.ApplyDoctorRequest
import com.hellodoc.healthcaresystem.requestmodel.ModifyClinic
import com.hellodoc.healthcaresystem.retrofit.RetrofitInstance
import com.hellodoc.healthcaresystem.responsemodel.GetDoctorResponse
import com.hellodoc.healthcaresystem.responsemodel.PendingDoctorResponse
import com.hellodoc.healthcaresystem.responsemodel.ServiceInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
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
            Log.e("DoctorViewModel", "Error preparing file part", e)
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

    var updateSuccess by mutableStateOf<Boolean?>(null)
        private set
    fun resetUpdateStatus() {
        updateSuccess = null
    }
    fun updateClinic(clinicUpdateData: ModifyClinic, doctorId: String, context: Context) {
        viewModelScope.launch {
            try {
                val gson = Gson()

                val addressPart = MultipartBody.Part.createFormData("address", clinicUpdateData.address)
                val descriptionPart = MultipartBody.Part.createFormData("description", clinicUpdateData.description)

                val workHourJson = gson.toJson(clinicUpdateData.workingHours)
                val workHourPart = MultipartBody.Part.createFormData("workingHours", workHourJson)

                // Dữ liệu services không gửi ảnh qua JSON
                val servicesJsonList = clinicUpdateData.services.map {
                    ServiceInput(
                        specialtyName = it.specialtyName,
                        minprice = it.minprice,
                        maxprice = it.maxprice,
                        description = it.description,
                        imageService = emptyList() // tránh gửi uri dạng content:// vào JSON
                    )
                }
                val servicesJson = gson.toJson(servicesJsonList)
                val servicesPart = MultipartBody.Part.createFormData("services", servicesJson)

                // Tạo danh sách ảnh
                val imageParts = mutableListOf<MultipartBody.Part>()
                clinicUpdateData.services.forEachIndexed { serviceIndex, service ->
                    service.imageService.forEachIndexed { imageIndex, uri ->
                        val partName = "image_${serviceIndex}_$imageIndex"
                        val part = prepareFilePart(context, uri, partName)
                        if (part != null) imageParts.add(part)
                    }
                }

                // Gọi API Retrofit
                val response = RetrofitInstance.doctor.updateClinic(
                    doctorId,
                    addressPart,
                    descriptionPart,
                    workHourPart,
                    servicesPart,
                    imageParts
                )

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Cập nhật phòng khám thành công!", Toast.LENGTH_LONG).show()
                        updateSuccess = true
                        Log.d("UpdateClinic", "Thành công: ${response.body()}")
                    } else {
                        val errorBody = response.errorBody()?.string()
                        updateSuccess = false
                        Toast.makeText(context, "Lỗi cập nhật: $errorBody", Toast.LENGTH_LONG).show()
                        Log.e("UpdateClinic", "Lỗi: $errorBody")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    updateSuccess = false
                    Toast.makeText(context, "Thay đổi thất bại: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e("UpdateClinic", "Exception", e)
                }
            }
        }
    }


    private val _pendingDoctors = MutableStateFlow<List<PendingDoctorResponse>>(emptyList())
    val pendingDoctors: StateFlow<List<PendingDoctorResponse>> get() = _pendingDoctors

    fun fetchPendingDoctor() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.doctor.getPendingDoctor()
                if (response.isSuccessful) {
                    _pendingDoctors.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                Log.e("lấy pending doctor", "Thất bại ", e)
                e.printStackTrace()
            }
        }
    }

    private val _pendingDoctor = MutableStateFlow<PendingDoctorResponse?>(null)
    val pendingDoctor: StateFlow<PendingDoctorResponse?> get() = _pendingDoctor

    fun fetchPendingDoctorById(userId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.doctor.getPendingDoctorById(userId)
                if (response.isSuccessful) {
                    _pendingDoctor.value = response.body()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deletePendingDoctor(userId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.doctor.deletePendingDoctorById(userId)
                if(response.isSuccessful) {
                    fetchPendingDoctor()
                    Log.d("Xoa pending doctor", " thanh cong")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val _verificationMessage  = MutableStateFlow<String?>(null)
    val verificationMessage : StateFlow<String?> = _verificationMessage

    fun verifyDoctor(userId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.doctor.verifyDoctor(userId)
                if(response.isSuccessful) {
                    fetchPendingDoctor()
                    Log.d("verify pending doctor", " thanh cong")
                    val message = response.body()?.message
                    if (message?.contains("thành công", ignoreCase = true) == true) {
                        _verificationMessage.value = "success"
                    } else {
                        _verificationMessage.value = "fail"
                    }
                }
                else {
                    _verificationMessage.value = "fail"
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchDoctorWithStats(doctorId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val doctorRes = RetrofitInstance.doctor.getDoctorById(doctorId)
                val statsRes = RetrofitInstance.appointment.getDoctorStats(doctorId)

                if (doctorRes.isSuccessful && statsRes.isSuccessful) {
                    val doctor = doctorRes.body()
                    val stats = statsRes.body()

                    _doctor.value = doctor?.copy(
                        patientsCount = stats?.patientsCount ?: 0,
                        ratingsCount = stats?.ratingsCount ?: 0
                    )
                } else {
                    println("Lỗi lấy doctor/stats: ${doctorRes.errorBody()?.string()}, ${statsRes.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }


}


