package com.hellodoc.healthcaresystem.model.repository

import com.hellodoc.healthcaresystem.api.AppointmentService
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.AppointmentResponse
import com.hellodoc.healthcaresystem.requestmodel.CreateAppointmentRequest
import com.hellodoc.healthcaresystem.requestmodel.UpdateAppointmentRequest
import com.hellodoc.healthcaresystem.roomDb.data.dao.AppointmentDao
import com.hellodoc.healthcaresystem.roomDb.data.entity.AppointmentEntity
import javax.inject.Inject

class AppointmentRepository @Inject constructor(
    private val appointmentDao: AppointmentDao,
    private val appointmentService: AppointmentService
){
    suspend fun getAllAppointments() = appointmentService.getAllAppointments()
    suspend fun getAppointmentUser(
        patientId: String
    ) = appointmentService.getAppointmentUser(patientId)
    suspend fun getAppointmentDoctor(
        doctorId: String
    ) = appointmentService.getAppointmentDoctor(doctorId)
    suspend fun createAppointment(
        token: String,
        createAppointmentRequest: CreateAppointmentRequest
    ) = appointmentService.createAppointment(
        token,
        createAppointmentRequest
    )
    suspend fun cancelAppointment(
        appointmentId: String
    ) = appointmentService.cancelAppointment(appointmentId)
    suspend fun deleteAppointmentById(
        appointmentId: String
    ) = appointmentService.deleteAppointmentById(appointmentId)
    suspend fun confirmAppointment(
        appointmentId: String
    ) = appointmentService.confirmAppointment(appointmentId)
    suspend fun updateAppointment(
        appointmentId: String,
        appointmentData: UpdateAppointmentRequest
    ) = appointmentService.updateAppointment(
        appointmentId,
        appointmentData
    )
    suspend fun getDoctorStats(
        doctorId: String
    ) = appointmentService.getDoctorStats(doctorId)


    suspend fun clearAppointments() = appointmentDao.clearAppointments()
    suspend fun insertAppointments(appointments: List<AppointmentEntity>) = appointmentDao.insertAppointments(appointments)
    suspend fun getAllAppointmentsFromRoom() = appointmentDao.getAllAppointments()
    suspend fun getDoctorAppointmentsFromRoom(doctorId: String) = appointmentDao.getDoctorAppointments(doctorId)
    suspend fun clearPatientAppointments(patientId: String) = appointmentDao.clearPatientAppointments(patientId)
    suspend fun clearDoctorAppointments(doctorId: String) = appointmentDao.clearDoctorAppointments(doctorId)
    suspend fun getPatientAppointments(
        patientId: String
    ) = appointmentDao.getPatientAppointments(
        patientId
    )
}