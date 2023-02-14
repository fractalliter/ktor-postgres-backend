package com.personia.dto

import kotlinx.serialization.Serializable


@Serializable
data class UserDTO(val username: String, val password: CharArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserDTO

        if (username != other.username) return false
        if (!password.contentEquals(other.password)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = username.hashCode()
        result = 31 * result + password.contentHashCode()
        return result
    }
}
