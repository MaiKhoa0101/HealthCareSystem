package com.hellodoc.healthcaresystem.model.repository

import com.hellodoc.healthcaresystem.api.DoctorService
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.GetDoctorResponse
import okhttp3.MultipartBody
import javax.inject.Inject

class DoctorRepository @Inject constructor(
    private val doctorService: DoctorService
) {
    suspend fun getDoctors() = doctorService.getDoctors()
    suspend fun getDoctorById(doctorId: String) = doctorService.getDoctorById(doctorId)
    suspend fun getDoctorBySpecialtyName(specialtyName: String) = doctorService.getDoctorBySpecialtyName(specialtyName)
    suspend fun getDoctorByName(name: String) = doctorService.getDoctorByName(name)
    suspend fun fetchAvailableSlots(
        doctorId: String
    ) = doctorService.fetchAvailableSlots(doctorId)

    suspend fun applyForDoctor(
        userId: String,
        license: MultipartBody.Part,
        specialty: MultipartBody.Part,
        CCCD: MultipartBody.Part,
        address: MultipartBody.Part,
        licenseUrl: MultipartBody.Part?,
        faceUrl: MultipartBody.Part?,
        avatarURL: MultipartBody.Part?,
        frontCccdUrl: MultipartBody.Part?,
        backCccdUrl: MultipartBody.Part?
    ) = doctorService.applyForDoctor(
        userId,
        license,
        specialty,
        CCCD,
        address,
        licenseUrl,
        faceUrl,
        avatarURL,
        frontCccdUrl,
        backCccdUrl
    )

    suspend fun updateClinic(
        doctorId: String,
        address: MultipartBody.Part,
        description: MultipartBody.Part,
        workingHours: MultipartBody.Part,
        oldWorkingHours: MultipartBody.Part,
        services: MultipartBody.Part,
        images: List<MultipartBody.Part>,
        oldService:MultipartBody.Part,
        hasHomeService:MultipartBody.Part,
        isClinicPaused:MultipartBody.Part
    ) = doctorService.updateClinic(
        doctorId,
        address,
        description,
        workingHours,
        oldWorkingHours,
        services,
        images,
        oldService,
        hasHomeService,
        isClinicPaused
    )

    suspend fun getPendingDoctor() = doctorService.getPendingDoctor()
    suspend fun getPendingDoctorById(id: String) = doctorService.getPendingDoctorById(id)
    suspend fun deletePendingDoctorById(id: String) = doctorService.deletePendingDoctorById(id)
    suspend fun verifyDoctor(id: String) = doctorService.verifyDoctor(id)

}