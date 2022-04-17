package com.personia.services

import com.personia.dao.user.userDAO
import com.personia.models.User
import com.personia.utils.Hasher
import io.ktor.server.plugins.*

class UserService {

    suspend fun createUser(user: User): User? {
        val hashedPassword = Hasher.hashPassword(user.password)
        val newUser = User(username = user.username, password = hashedPassword)
        return userDAO.createUser(newUser)
    }

    suspend fun loginUser(username: String, password: String): Boolean {
        val user = userDAO.findByUsername(username)
        if (user != null){
            if (Hasher.checkPassword(password, user)) return true
            else {
                throw SecurityException("Wrong Password")
            }
        } else throw NotFoundException("user not found")
    }
}

val userService = UserService()