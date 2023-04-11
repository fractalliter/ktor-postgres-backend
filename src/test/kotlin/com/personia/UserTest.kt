package com.personia

import com.personia.plugins.configureRouting
import com.personia.utils.randomString
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UserTest {

    @Test
    fun testAuthFlow() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        environment {
            config = ApplicationConfig("application-test.conf")
        }
        application {
            configureRouting(environment.config)
        }

        val username = randomString(10)
        val password = randomString(10)

        // Test: sing up user
        val responseSignUp = client.post("/signup") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("username" to username, "password" to password))
        }
        assertEquals(HttpStatusCode.Created, responseSignUp.status)
        assertEquals("User singed up", responseSignUp.bodyAsText())

        // Test: duplicate username exception
        val responseDupSignUp = client.post("/signup") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("username" to username, "password" to password))
        }
        assertEquals(HttpStatusCode.Forbidden, responseDupSignUp.status)
        assertTrue(responseDupSignUp.bodyAsText().contains("duplicate key value violates unique constraint."))

        // Test: login user
        val responseLogin = client.post("/login") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("username" to username, "password" to password))
        }
        assertEquals(HttpStatusCode.OK, responseLogin.status)
        assertTrue(responseLogin.bodyAsText().contains("token"))

        // Test: login with wrong password
        val responseWrongPassword = client.post("/login") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("username" to username, "password" to randomString(15)))
        }
        assertEquals(HttpStatusCode.BadRequest, responseWrongPassword.status)
        assertTrue(responseWrongPassword.bodyAsText().contains("Wrong Password"))

        // Test: login with username that doesn't exist in the system
        val responseUserNotFound = client.post("/login") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("username" to randomString(12), "password" to password))
        }
        assertEquals(HttpStatusCode.NotFound, responseUserNotFound.status)
        assertTrue(responseUserNotFound.bodyAsText().contains("user not found"))
    }
}