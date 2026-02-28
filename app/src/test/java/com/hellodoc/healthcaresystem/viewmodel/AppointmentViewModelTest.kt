package com.hellodoc.healthcaresystem.viewmodel

import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.AppointmentResponse
import com.hellodoc.healthcaresystem.model.repository.AppointmentRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

class AppointmentViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: AppointmentRepository = mockk(relaxed = true)
    private lateinit var viewModel: AppointmentViewModel

    @Test
    fun `fetchAppointments updates appointmentsUser on success`() = runTest {
        // Arrange
        val mockAppointments = listOf(
            AppointmentResponse(
                id = "1",
                doctor = AppointmentResponse.Doctor("d1", "Dr A", "Spec A", ""),
                patientModel = "",
                patient = AppointmentResponse.Patient("p1", "Pat A", ""),
                date = "2023-01-01",
                time = "10:00",
                status = "PENDING",
                examinationMethod = "Offline",
                notes = "notes",
                location = "loc",
                createdAt = "created",
                updatedAt = "updated",
                note = "note"
            )
        )
        coEvery { repository.getAllAppointments() } returns Response.success(mockAppointments)
        
        viewModel = AppointmentViewModel(repository)

        // Act
        viewModel.fetchAppointments()

        // Assert
        assertEquals(mockAppointments, viewModel.appointmentsUser.value)
        coVerify { repository.clearAppointments() }
        coVerify { repository.insertAppointments(any()) }
    }

    @Test
    fun `fetchAppointments loads from local on failure`() = runTest {
        // Arrange
        coEvery { repository.getAllAppointments() } returns Response.error(500, mockk(relaxed = true))
        
        // Mock local DB call requires mocking Entity -> Response conversion implicitly via repository behavior
        // Since we can't easily mock extensions, we rely on repository returning entities and verify flow behavior if possible.
        // However, ViewModel calls toEntity() and toResponse() which are extension functions.
        // Extension functions are static and hard to mock.
        // Instead, we can verify that repository.getAllAppointmentsFromRoom() is called.

        viewModel = AppointmentViewModel(repository)

        // Act
        viewModel.fetchAppointments()

        // Assert
        coVerify { repository.getAllAppointmentsFromRoom() }
    }
}
