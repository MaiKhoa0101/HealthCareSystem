package com.hellodoc.healthcaresystem.model.repository

import com.hellodoc.healthcaresystem.model.api.AppointmentService
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.AppointmentResponse
import com.hellodoc.healthcaresystem.roomDb.data.dao.AppointmentDao
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Response

class AppointmentRepositoryTest {

    private val appointmentDao: AppointmentDao = mockk(relaxed = true)
    private val appointmentService: AppointmentService = mockk(relaxed = true)
    private val repository = AppointmentRepositoryImpl(appointmentDao, appointmentService)

    @Test
    fun `getAllAppointments returns API response`() = runTest {
        // Arrange
        val mockResponse = listOf(AppointmentResponse(id = "1", doctor = mockk(), patient = mockk(), date = "2023-01-01", time = "10:00", status = "PENDING", examinationMethod = "Online", notes = "notes", location = "Zoom", createdAt = "now", updatedAt = "now", note = "note", patientModel = ""))
        coEvery { appointmentService.getAllAppointments() } returns Response.success(mockResponse)

        // Act
        val result = repository.getAllAppointments()

        // Assert
        assertEquals(true, result.isSuccessful)
        assertEquals(mockResponse, result.body())
        coVerify(exactly = 1) { appointmentService.getAllAppointments() }
    }

    @Test
    fun `clearAppointments calls DAO`() = runTest {
        // Act
        repository.clearAppointments()

        // Assert
        coVerify(exactly = 1) { appointmentDao.clearAppointments() }
    }

    @Test
    fun `getAppointmentUser calls API with correct ID`() = runTest {
        // Arrange
        val patientId = "pat123"
        val mockResponse = listOf<AppointmentResponse>()
        coEvery { appointmentService.getAppointmentUser(patientId) } returns Response.success(mockResponse)

        // Act
        val result = repository.getAppointmentUser(patientId)

        // Assert
        assertEquals(true, result.isSuccessful)
        assertEquals(mockResponse, result.body())
        coVerify(exactly = 1) { appointmentService.getAppointmentUser(patientId) }
    }
}
