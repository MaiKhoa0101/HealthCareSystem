package com.hellodoc.healthcaresystem.model.repository

import com.hellodoc.healthcaresystem.model.api.AdminService
import javax.inject.Inject

class AdminRepository @Inject constructor(
    private val adminService: AdminService
) {
    suspend fun getAllUsers() = adminService.getAllUser()
    suspend fun deleteUser(userId: String) = adminService.deleteUser(userId)
}
