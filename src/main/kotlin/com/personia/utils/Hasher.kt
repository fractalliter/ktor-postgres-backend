package com.personia.utils

import com.personia.models.User
import org.mindrot.jbcrypt.BCrypt

object Hasher {

    /**
     * Check if the password matches the User's password
     */
    fun checkPassword(attempt: CharArray, user: User) = BCrypt.checkpw(attempt.toString(), user.password)

    /**
     * Returns the hashed version of the supplied password
     */
    fun hashPassword(password: CharArray): String = BCrypt.hashpw(password.toString(), BCrypt.gensalt())

}