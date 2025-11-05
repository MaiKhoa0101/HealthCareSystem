package com.hellodoc.healthcaresystem.domain.repository

import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.User

interface UserRepository {
    suspend fun getUsers(): List<User>
}
