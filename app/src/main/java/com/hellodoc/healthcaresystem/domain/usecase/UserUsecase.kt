package com.hellodoc.healthcaresystem.domain.usecase

import com.hellodoc.healthcaresystem.domain.repository.UserRepository
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.User

class GetUsersUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): List<User> {
        return repository.getUsers()
    }
}
