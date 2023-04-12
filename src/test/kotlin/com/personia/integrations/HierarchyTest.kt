package com.personia.integrations

import com.google.gson.Gson
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
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

data class Token(val token: String)

class HierarchyTest {

    @Test
    fun testHierarchy() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        assertNotNull(client)
        environment {
            config = ApplicationConfig("application-test.conf")
        }
        application {
            configureRouting(environment.config)
        }

        val username = randomString(10)
        val password = randomString(10)
        val responseSignUp = client.post("/signup") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("username" to username, "password" to password))
        }
        assertEquals(HttpStatusCode.Created, responseSignUp.status)
        assertEquals("User singed up", responseSignUp.bodyAsText())

        // Testing login endpoint
        val responseLogin = client.post("/login") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("username" to username, "password" to password))
        }
        assertEquals(HttpStatusCode.OK, responseLogin.status)
        assertTrue(responseLogin.bodyAsText().contains("token"))
        val gson = Gson()
        val accessToken = gson.fromJson(responseLogin.bodyAsText(), Token::class.java)

        // Testing hierarchy endpoint
        val responseHierarchy = client.post("/hierarchy") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${accessToken.token}")
            setBody(mapOf("Nick" to "Sophie", "Sophie" to "Jonas", "Pete" to "Nick", "Barbara" to "Nick"))
        }
        assertEquals(HttpStatusCode.OK, responseHierarchy.status)
        assertTrue(responseHierarchy.bodyAsText().contains("Jonas"))

        val responseGetHierarchy = client.get("/hierarchy") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${accessToken.token}")
            setBody(mapOf("Nick" to "Sophie", "Sophie" to "Jonas", "Pete" to "Nick", "Barbara" to "Nick"))
        }
        assertEquals(HttpStatusCode.OK, responseGetHierarchy.status)
        assertTrue(responseGetHierarchy.bodyAsText().contains("Jonas"))

        // Testing supervisors
        val response = client.get("hierarchy/Nick/supervisors") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${accessToken.token}")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Jonas"))
    }
}