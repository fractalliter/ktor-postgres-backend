package com.personia.dao.user

import com.personia.dto.User

interface UserDaoFacade {
    suspend fun createUser(user: User): User?
    suspend fun findByUsername(username: String): User?
}