package com.hellodoc.healthcaresystem.model.repository

import com.hellodoc.healthcaresystem.model.api.AdminService
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.DeleteUserResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.UserResponse
import retrofit2.Response
import javax.inject.Inject

interface  AdminRepository{
    suspend fun getAllUsers(): Response<UserResponse>
    suspend fun deleteUser(userId: String): Response<DeleteUserResponse>
}
class AdminRepositoryImpl @Inject constructor(
    private val adminService: AdminService
): AdminRepository {
    override suspend fun getAllUsers() = adminService.getAllUser()
    override suspend fun deleteUser(userId: String) = adminService.deleteUser(userId)
}
