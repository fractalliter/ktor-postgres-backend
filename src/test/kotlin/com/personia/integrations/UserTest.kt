package com.personia.integrations

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

        val credentials = mapOf(
            "username" to randomString(10),
            "password" to randomString(10)
        )

        // Test: sing up user
        val responseSignUp = client.post("/signup") {
            contentType(ContentType.Application.Json)
            setBody(credentials)
        }
        assertEquals(HttpStatusCode.Created, responseSignUp.status)
        assertEquals("User singed up", responseSignUp.bodyAsText())

        // Test: duplicate username exception
        val responseDupSignUp = client.post("/signup") {
            contentType(ContentType.Application.Json)
            setBody(credentials)
        }
        assertEquals(HttpStatusCode.Forbidden, responseDupSignUp.status)
        assertTrue(responseDupSignUp.bodyAsText().contains("duplicate key value violates unique constraint."))

        // Test: login user
        val responseLogin = client.post("/login") {
            contentType(ContentType.Application.Json)
            setBody(credentials)
        }
        assertEquals(HttpStatusCode.OK, responseLogin.status)
        assertTrue(responseLogin.bodyAsText().contains("token"))

        // Test: login with wrong password
        val wrongPasswordCredential = credentials + mapOf("password" to randomString(15))
        val responseWrongPassword = client.post("/login") {
            contentType(ContentType.Application.Json)
            setBody(wrongPasswordCredential)
        }
        assertEquals(HttpStatusCode.BadRequest, responseWrongPassword.status)
        assertTrue(responseWrongPassword.bodyAsText().contains("Wrong Password"))

        // Test: login with username that doesn't exist in the system
        val wrongUsernameCredential = credentials + mapOf("username" to randomString(12))
        val responseUserNotFound = client.post("/login") {
            contentType(ContentType.Application.Json)
            setBody(wrongUsernameCredential)
        }
        assertEquals(HttpStatusCode.NotFound, responseUserNotFound.status)
        assertTrue(responseUserNotFound.bodyAsText().contains("user not found"))
    }
}