package com.personia.integrations

import com.personia.configIntegrationTestApp
import com.personia.utils.randomString
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UserTest {

    private val credentials = mapOf(
        "username" to randomString(10),
        "password" to randomString(10)
    )
    @Test
    fun testSignUp() = testApplication {
        val client = configIntegrationTestApp()

        val responseSignUp = client.post("/signup") {
            contentType(ContentType.Application.Json)
            setBody(credentials)
        }
        assertEquals(HttpStatusCode.Created, responseSignUp.status)
        assertEquals("User singed up", responseSignUp.bodyAsText())
    }

    @Test
    fun testDuplicateSignup() = testApplication{
        val client = configIntegrationTestApp()
        client.post("/signup") {
            contentType(ContentType.Application.Json)
            setBody(credentials)
        }
        val responseDupSignUp = client.post("/signup") {
            contentType(ContentType.Application.Json)
            setBody(credentials)
        }
        assertEquals(HttpStatusCode.BadRequest, responseDupSignUp.status)
        assertTrue(responseDupSignUp.bodyAsText().contains("duplicate key value violates unique constraint."))
    }

    @Test
    fun testLogin() = testApplication {
        val client = configIntegrationTestApp()
        client.post("/signup") {
            contentType(ContentType.Application.Json)
            setBody(credentials)
        }
        val responseLogin = client.post("/login") {
            contentType(ContentType.Application.Json)
            setBody(credentials)
        }
        assertEquals(HttpStatusCode.OK, responseLogin.status)
        assertTrue(responseLogin.bodyAsText().contains("token"))
    }

    @Test
    fun testLoginWrongPass()= testApplication {
        val client = configIntegrationTestApp()
        client.post("/signup") {
            contentType(ContentType.Application.Json)
            setBody(credentials)
        }
        val wrongPasswordCredential = credentials + mapOf("password" to randomString(15))
        val responseWrongPassword = client.post("/login") {
            contentType(ContentType.Application.Json)
            setBody(wrongPasswordCredential)
        }
        assertEquals(HttpStatusCode.BadRequest, responseWrongPassword.status)
        assertTrue(responseWrongPassword.bodyAsText().contains("Wrong Password"))
    }

    @Test
    fun testLoginForNonExistingUser() = testApplication {
        val client = configIntegrationTestApp()
        val wrongUsernameCredential = credentials + mapOf("username" to randomString(12))
        val responseUserNotFound = client.post("/login") {
            contentType(ContentType.Application.Json)
            setBody(wrongUsernameCredential)
        }
        assertEquals(HttpStatusCode.NotFound, responseUserNotFound.status)
        assertTrue(responseUserNotFound.bodyAsText().contains("user not found"))
    }
}