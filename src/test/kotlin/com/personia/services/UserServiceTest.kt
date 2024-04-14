package com.personia.services

import com.personia.configureUnitTestApp
import com.personia.dto.User
import com.personia.utils.Hasher
import com.personia.utils.randomString
import io.ktor.server.testing.*
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class UserServiceTest {
    private val username = randomString(12)
    private val password = randomString(16)

    @Test
    fun createUser() = testApplication {
        configureUnitTestApp()
        val user = userService.createUser(
            User(
                username = username,
                password = password
            )
        )

        assertNotNull(user)
        assertNotNull(user.id)
        assertEquals(username, user.username)
        assertNotEquals(password, user.password)
        assertEquals(Hasher.hashPassword(password), user.password)
    }

    @Test
    fun loginUser() = testApplication {
        configureUnitTestApp()
        userService.createUser(
            User(
                username = username,
                password = password
            )
        )
        val res = userService.loginUser(username, password)
        assertTrue(res)
    }
}