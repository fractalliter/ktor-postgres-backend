package com.personia.dao.user

import com.personia.models.User

interface UserDaoFacade {
    suspend fun createUser(user:User): User?
    suspend fun findByUsername(username: String): User?
}