package com.hellodoc.healthcaresystem.model.roomDb.mapper

import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.AppointmentResponse
import com.hellodoc.healthcaresystem.model.roomDb.data.entity.AppointmentEntity
import com.hellodoc.healthcaresystem.roomDb.mapper.toEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class AppointmentMapperTest {

    @Test
    fun `toEntity converts AppointmentResponse to AppointmentEntity correctly`() {
        // Arrange
        val response = AppointmentResponse(
            id = "123",
            doctor = AppointmentResponse.Doctor(
                id = "doc1",
                name = "Dr. House",
                specialty = "Diagnostician",
                avatarURL = "http://avatar.com/house.jpg"
            ),
            patientModel = "",
            patient = AppointmentResponse.Patient(
                id = "pat1",
                name = "Patient Zero",
                avatarURL = "http://avatar.com/zero.jpg"
            ),
            date = "2023-10-27",
            time = "10:00",
            status = "CONFIRMED",
            examinationMethod = "Offline",
            notes = "Checkup",
            location = "Room 101",
            createdAt = "1000",
            updatedAt = "2000",
            note = "Headache"
        )

        // Act
        val entity = response.toEntity()

        // Assert
        assertEquals("123", entity.id)
        assertEquals("doc1", entity.doctorId)
        assertEquals("pat1", entity.patientId)
        assertEquals("Dr. House", entity.doctorName)
        assertEquals("Diagnostician", entity.specialtyName)
        assertEquals("Patient Zero", entity.patientName)
        assertEquals("2023-10-27", entity.appointmentDate)
        assertEquals("10:00", entity.appointmentTime)
        assertEquals("CONFIRMED", entity.status)
        assertEquals("Headache", entity.reason)
        assertEquals("Checkup", entity.notes)
        assertEquals("Room 101", entity.location)
    }
}
