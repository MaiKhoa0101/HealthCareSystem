package com.hellodoc.healthcaresystem.model.repository

import com.hellodoc.healthcaresystem.api.DoctorService
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.GetDoctorResponse
import javax.inject.Inject

class DoctorRepository @Inject constructor(
    private val doctorService: DoctorService
) {
    suspend fun getDoctors() = doctorService.getDoctors()
    suspend fun getDoctorById(doctorId: String) = doctorService.getDoctorById(doctorId)
    suspend fun getDoctorBySpecialtyName(specialtyName: String) = doctorService.getDoctorBySpecialtyName(specialtyName)
    suspend fun getDoctorByName(name: String) = doctorService.getDoctorByName(name)
    



}