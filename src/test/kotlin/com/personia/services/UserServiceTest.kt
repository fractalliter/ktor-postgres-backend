package com.personia.services

import com.personia.dto.User
import com.personia.utils.randomString
import io.ktor.server.config.*
import io.ktor.server.testing.*
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class UserServiceTest {

    private fun userServiceTest(test: suspend () -> Unit) = testApplication {
        createClient {  }
        environment {
            config = ApplicationConfig("application-test.conf")
        }
        test()
    }

    @Test
    fun createUser() = userServiceTest {
        val user = userService.createUser(
            User(
                username = randomString(12),
                password = randomString(20)
            )
        )

        assertNotNull(user)
        assertNotNull(user.id)
    }

    @Test
    fun loginUser() = userServiceTest {
        val username = randomString(12)
        val password = randomString(16)
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