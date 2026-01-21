package com.hellodoc.healthcaresystem.model.repository

import com.hellodoc.healthcaresystem.model.api.AppointmentService
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.AppointmentResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.CancelAppointmentResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.CreateAppointmentResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.DoctorStatsResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.SuggestedAppointmentResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.UpdateAppointmentResponse
import com.hellodoc.healthcaresystem.requestmodel.CreateAppointmentRequest
import com.hellodoc.healthcaresystem.requestmodel.SuggestedAppointmentRequest
import com.hellodoc.healthcaresystem.requestmodel.UpdateAppointmentRequest
import com.hellodoc.healthcaresystem.roomDb.data.dao.AppointmentDao
import com.hellodoc.healthcaresystem.model.roomDb.data.entity.AppointmentEntity
import retrofit2.Response
import javax.inject.Inject

interface AppointmentRepository{
    suspend fun getAllAppointments(): Response<List<AppointmentResponse>>
    suspend fun getAppointmentUser(
        patientId: String
    ): Response<List<AppointmentResponse>>
    suspend fun getAppointmentDoctor(
        doctorId: String
    ): Response<List<AppointmentResponse>>

    suspend fun createAppointment(
        token: String,
        createAppointmentRequest: CreateAppointmentRequest
    ): Response<CreateAppointmentResponse>

    suspend fun cancelAppointment(
        appointmentId: String
    ): Response<CancelAppointmentResponse>


    suspend fun deleteAppointmentById(
        appointmentId: String
    ): Response<UpdateAppointmentResponse>


    suspend fun confirmAppointment(
        appointmentId: String
    ): Response<UpdateAppointmentResponse>


    suspend fun updateAppointment(
        appointmentId: String,
        appointmentData: UpdateAppointmentRequest
    ): Response<UpdateAppointmentResponse>

    suspend fun getDoctorStats(
        doctorId: String
    ):Response<DoctorStatsResponse>


    suspend fun getSuggestedAppointments(
        request: SuggestedAppointmentRequest
    ): Response<List<SuggestedAppointmentResponse>>


    suspend fun clearAppointments()
    suspend fun insertAppointments(appointments: List<AppointmentEntity>)
    suspend fun getAllAppointmentsFromRoom(): List<AppointmentEntity>

    suspend fun getDoctorAppointmentsFromRoom(doctorId: String): List<AppointmentEntity>

    suspend fun clearPatientAppointments(patientId: String)
    suspend fun clearDoctorAppointments(doctorId: String)

    suspend fun getPatientAppointments(
        patientId: String
    ): List<AppointmentEntity>

}
class AppointmentRepositoryImpl @Inject constructor(
    private val appointmentDao: AppointmentDao,
    private val appointmentService: AppointmentService
): AppointmentRepository {
    override suspend fun getAllAppointments() = appointmentService.getAllAppointments()
    override suspend fun getAppointmentUser(
        patientId: String
    ) = appointmentService.getAppointmentUser(patientId)
    override suspend fun getAppointmentDoctor(
        doctorId: String
    ) = appointmentService.getAppointmentDoctor(doctorId)
    override suspend fun createAppointment(
        token: String,
        createAppointmentRequest: CreateAppointmentRequest
    ) = appointmentService.createAppointment(
        token,
        createAppointmentRequest
    )


    override suspend fun cancelAppointment(
        appointmentId: String
    ) = appointmentService.cancelAppointment(appointmentId)
    override suspend fun deleteAppointmentById(
        appointmentId: String
    ) = appointmentService.deleteAppointmentById(appointmentId)
    override suspend fun confirmAppointment(
        appointmentId: String
    ) = appointmentService.confirmAppointment(appointmentId)
    override suspend fun updateAppointment(
        appointmentId: String,
        appointmentData: UpdateAppointmentRequest
    ) = appointmentService.updateAppointment(
        appointmentId,
        appointmentData
    )
    override suspend fun getDoctorStats(
        doctorId: String
    ) = appointmentService.getDoctorStats(doctorId)

    override suspend fun getSuggestedAppointments(
        request: SuggestedAppointmentRequest
    ) = appointmentService.getSuggestedAppointments(request)


    override suspend fun clearAppointments() = appointmentDao.clearAppointments()
    override suspend fun insertAppointments(appointments: List<AppointmentEntity>) = appointmentDao.insertAppointments(appointments)
    override suspend fun getAllAppointmentsFromRoom() = appointmentDao.getAllAppointments()
    override suspend fun getDoctorAppointmentsFromRoom(doctorId: String) = appointmentDao.getDoctorAppointments(doctorId)
    override suspend fun clearPatientAppointments(patientId: String) = appointmentDao.clearPatientAppointments(patientId)
    override suspend fun clearDoctorAppointments(doctorId: String) = appointmentDao.clearDoctorAppointments(doctorId)
    override suspend fun getPatientAppointments(
        patientId: String
    ) = appointmentDao.getPatientAppointments(
        patientId
    )
}