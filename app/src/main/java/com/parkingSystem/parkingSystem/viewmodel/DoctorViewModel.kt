package com.parkingSystem.parkingSystem.viewmodel

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
import com.parkingSystem.parkingSystem.requestmodel.ApplyDoctorRequest
import com.parkingSystem.parkingSystem.requestmodel.ModifyClinicRequest
import com.parkingSystem.parkingSystem.responsemodel.DoctorAvailableSlotsResponse
import com.parkingSystem.parkingSystem.retrofit.RetrofitInstance
import com.parkingSystem.parkingSystem.responsemodel.GetDoctorResponse
import com.parkingSystem.parkingSystem.responsemodel.PendingDoctorResponse
import com.parkingSystem.parkingSystem.responsemodel.ServiceInput
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

    private val _availableWorkingHours = MutableStateFlow<DoctorAvailableSlotsResponse?>(null)
    val availableWorkingHours: StateFlow<DoctorAvailableSlotsResponse?> get() = _availableWorkingHours

    fun fetchAvailableSlots(doctorId: String){
        viewModelScope.launch {
            try{
                val response = RetrofitInstance.doctor.fetchAvailableSlots(doctorId)
                if(response.isSuccessful){
                    _availableWorkingHours.value = response.body()
                } else {
                    println("Lỗi API: ${response.errorBody()?.string()}")
                }
            } catch(e: Exception){
                e.printStackTrace()
            }
        }
    }

    fun fetchDoctors(forceRefresh: Boolean = false) {

        // Tránh gọi lại nếu đã có dữ liệu và không yêu cầu refresh
        if (_doctors.value.isNotEmpty() && !forceRefresh) return

        viewModelScope.launch {
            try {
                val response = RetrofitInstance.doctor.getDoctors()
                if (response.isSuccessful) {
                    _doctors.value = response.body() ?: emptyList()
                    //println("OK 1" + response.body())
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

    private val _applyMessage  = MutableStateFlow<String?>(null)
    val applyMessage : StateFlow<String?> = _applyMessage
    val loading = mutableStateOf(false)

    fun setApplyMessage(value: String) {
        _applyMessage.value = value
    }

    fun applyForDoctor(userId: String, request: ApplyDoctorRequest, context: Context) {
        loading.value = true

        viewModelScope.launch {
            try {
                // Tạo multipart từ các trường bắt buộc
                val license = MultipartBody.Part.createFormData("license", request.license)
                val specialty = MultipartBody.Part.createFormData("specialty", request.specialty)
                val CCCD = MultipartBody.Part.createFormData("CCCD", request.CCCD)
                val address = MultipartBody.Part.createFormData("address", request.address)

                // Tạo multipart từ các file nếu có
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

                // Gọi API
                val response = RetrofitInstance.doctor.applyForDoctor(
                    userId,
                    license,
                    specialty,
                    address,
                    CCCD,
                    licenseUrl,
                    faceUrl,
                    avatarURL,
                    frontCccdUrl,
                    backCccdUrl
                )

                withContext(Dispatchers.Main) {
                    val successKeywords = listOf("thành công", "đã gửi", "trước đó", "hoàn tất")

                    if (response.isSuccessful) {
                        val message = response.body()?.message?.lowercase().orEmpty()

                        Toast.makeText(
                            context,
                            "Đăng ký thành công. Vui lòng chờ xác thực.",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.d("Đăng ký Bsi", "Đăng ký bác sĩ thành công: $message")

                        if (successKeywords.any { message.contains(it) }) {
                            _applyMessage.value = "success"
                            Log.d("Đăng ký Bsi", "Set applyMessage = success (body)")
                        } else {
                            _applyMessage.value = "fail"
                            Log.d("Đăng ký Bsi", "Set applyMessage = fail (body message không hợp lệ)")
                        }
                    } else {
                        // Lấy nội dung lỗi từ server
                        val errorMsg = response.errorBody()?.string()?.lowercase().orEmpty()
                        Log.e("Đăng ký Bsi", "Đăng ký thất bại - error body: $errorMsg")

                        if (successKeywords.any { errorMsg.contains(it) }) {
                            _applyMessage.value = "success"
                            Toast.makeText(
                                context,
                                "Bạn đã gửi yêu cầu trước đó.",
                                Toast.LENGTH_LONG
                            ).show()
                            Log.d("Đăng ký Bsi", "Set applyMessage = success (từ error)")
                        } else {
                            _applyMessage.value = "fail"
                            Toast.makeText(
                                context,
                                "Đăng ký bác sĩ thất bại.",
                                Toast.LENGTH_LONG
                            ).show()
                            Log.d("Đăng ký Bsi", "Set applyMessage = fail (error không hợp lệ)")
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Đăng ký bác sĩ thất bại: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    _applyMessage.value = "fail"
                    Log.e("Đăng ký Bsi", "Exception khi đăng ký bác sĩ", e)
                }
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
    // --- ViewModel Function ---
    fun updateClinic(clinicUpdateData: ModifyClinicRequest, doctorId: String, context: Context) {
        viewModelScope.launch {
            try {
                val gson = Gson()
                val oldservicesJson = gson.toJson(clinicUpdateData.oldServices)
                val oldservicesPart = MultipartBody.Part.createFormData("oldService", oldservicesJson)

                val addressPart = MultipartBody.Part.createFormData("address", clinicUpdateData.address)
                val descriptionPart = MultipartBody.Part.createFormData("description", clinicUpdateData.description)

                val workHourJson = gson.toJson(clinicUpdateData.workingHours)
                val workHourPart = MultipartBody.Part.createFormData("workingHours", workHourJson)

                val oldWorkHourJson = gson.toJson(clinicUpdateData.oldWorkingHours)
                val oldWorkHourPart = MultipartBody.Part.createFormData("oldWorkingHours", oldWorkHourJson)

                val hasHomeServicePart = MultipartBody.Part.createFormData("hasHomeService", clinicUpdateData.hasHomeService.toString())
                val isClinicPausedPart = MultipartBody.Part.createFormData("isClinicPaused", clinicUpdateData.isClinicPaused.toString())

                val servicesJsonList = clinicUpdateData.services.map {
                    ServiceInput(
                        specialtyId = it.specialtyId,
                        specialtyName = it.specialtyName,
                        minprice = it.minprice,
                        maxprice = it.maxprice,
                        description = it.description,
                        imageService = emptyList()
                    )
                }
                val servicesJson = gson.toJson(servicesJsonList)
                val servicesPart = MultipartBody.Part.createFormData("services", servicesJson)

                val imageParts = mutableListOf<MultipartBody.Part>()
                clinicUpdateData.services.forEachIndexed { serviceIndex, service ->
                    service.imageService.forEachIndexed { imageIndex, uri ->
                        val partName = "image_${serviceIndex}_$imageIndex"
                        val part = prepareFilePart(context, uri, partName)
                        if (part != null) imageParts.add(part)
                    }
                }
                println("Id doctor da ra: "+doctorId)
                println("Du lieu da ra: "+clinicUpdateData)
                val response = RetrofitInstance.doctor.updateClinic(
                    doctorId,
                    addressPart,
                    descriptionPart,
                    workHourPart,
                    oldWorkHourPart,
                    servicesPart,
                    imageParts,
                    oldservicesPart,
                    hasHomeServicePart,
                    isClinicPausedPart
                )
                println("Ket qua cap nhat: "+response.body())
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Cập nhật phòng khám thành công!", Toast.LENGTH_LONG).show()
                        updateSuccess = true
                    } else {
                        val errorBody = response.errorBody()?.string()
                        updateSuccess = false
                        Toast.makeText(context, "Lỗi cập nhật: $errorBody", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    updateSuccess = false
                    Toast.makeText(context, "Thay đổi thất bại: ${e.message}", Toast.LENGTH_LONG).show()
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

    private val _isLoadingStats = MutableStateFlow(false)
    val isLoadingStats: StateFlow<Boolean> get() = _isLoadingStats

    fun fetchDoctorWithStats(doctorId: String) {
        viewModelScope.launch {
            try {
                _isLoadingStats.value = true

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
                _isLoadingStats.value = false
            }
        }
    }



    fun resetStates() {
        _isLoading.value = false
    }
}


