package com.personia.services

import com.personia.dao.user.UserDaoFacade
import com.personia.dao.user.userDAO
import com.personia.dto.User
import com.personia.utils.Hasher
import io.ktor.server.plugins.*

class UserService(private val userRepository: UserDaoFacade) {

    suspend fun createUser(user: User): User? {
        val hashedPassword = Hasher.hashPassword(user.password)
        val newUser = User(username = user.username, password = hashedPassword)
        return userRepository.createUser(newUser)
    }

    suspend fun loginUser(username: String, password: String): Boolean {
        val user = userRepository.findByUsername(username)
        if (user != null) {
            if (Hasher.checkPassword(password, user)) return true
            else {
                throw AssertionError("Wrong Password")
            }
        } else throw NotFoundException("user not found")
    }
}

val userService = UserService(userDAO)