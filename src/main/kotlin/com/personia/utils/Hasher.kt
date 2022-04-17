package com.personia.utils

import com.personia.models.User
import org.mindrot.jbcrypt.BCrypt

object Hasher {

    /**
     * Check if the password matches the User's password
     */
    fun checkPassword(attempt: String, user: User) = BCrypt.checkpw(attempt, user.password)

    /**
     * Returns the hashed version of the supplied password
     */
    fun hashPassword(password: String): String = BCrypt.hashpw(password, BCrypt.gensalt())

}