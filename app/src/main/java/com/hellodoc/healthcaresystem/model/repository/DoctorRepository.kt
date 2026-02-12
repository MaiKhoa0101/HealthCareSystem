package com.hellodoc.healthcaresystem.model.repository

import com.hellodoc.healthcaresystem.api.DoctorService
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.ApplyDoctor
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.DoctorAvailableSlotsResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.GetDoctorResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.PendingDoctorResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.ReturnPendingDoctorResponse
import com.hellodoc.healthcaresystem.requestmodel.ModifyClinicRequest
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

interface DoctorRepository{
    suspend fun getDoctors(): Response<List<GetDoctorResponse>>
    suspend fun getDoctorById(doctorId: String): Response<GetDoctorResponse>

    suspend fun getDoctorBySpecialtyName(specialtyName: String): Response<List<GetDoctorResponse>>

    suspend fun getDoctorByName(name: String):  Response<List<GetDoctorResponse>>


    suspend fun fetchAvailableSlots(
        doctorId: String
    ): Response<DoctorAvailableSlotsResponse>

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

    ) :Response<ApplyDoctor>

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
        isClinicPaused:MultipartBody.Part,
        specialty: MultipartBody.Part

    ):Response<ModifyClinicRequest>

    suspend fun getPendingDoctor(): Response<List<PendingDoctorResponse>>
    suspend fun getPendingDoctorById(id: String): Response<PendingDoctorResponse>
    suspend fun deletePendingDoctorById(id: String):Response<ReturnPendingDoctorResponse>
    suspend fun verifyDoctor(id: String):Response<ReturnPendingDoctorResponse>
}

class DoctorRepositoryImpl @Inject constructor(
    private val doctorService: DoctorService
): DoctorRepository {
    override suspend fun getDoctors() = doctorService.getDoctors()
    override suspend fun getDoctorById(doctorId: String) = doctorService.getDoctorById(doctorId)
    override suspend fun getDoctorBySpecialtyName(specialtyName: String) = doctorService.getDoctorBySpecialtyName(specialtyName)
    override suspend fun getDoctorByName(name: String) = doctorService.getDoctorByName(name)
    override suspend fun fetchAvailableSlots(
        doctorId: String
    ) = doctorService.fetchAvailableSlots(doctorId)

    override suspend fun applyForDoctor(
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

    override suspend fun updateClinic(
        doctorId: String,
        address: MultipartBody.Part,
        description: MultipartBody.Part,
        workingHours: MultipartBody.Part,
        oldWorkingHours: MultipartBody.Part,
        services: MultipartBody.Part,
        images: List<MultipartBody.Part>,
        oldService:MultipartBody.Part,
        hasHomeService:MultipartBody.Part,
        isClinicPaused:MultipartBody.Part,
        specialty: MultipartBody.Part
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
        isClinicPaused,
        specialty
    )

    override suspend fun getPendingDoctor() = doctorService.getPendingDoctor()
    override suspend fun getPendingDoctorById(id: String) = doctorService.getPendingDoctorById(id)
    override suspend fun deletePendingDoctorById(id: String) = doctorService.deletePendingDoctorById(id)
    override suspend fun verifyDoctor(id: String) = doctorService.verifyDoctor(id)

}