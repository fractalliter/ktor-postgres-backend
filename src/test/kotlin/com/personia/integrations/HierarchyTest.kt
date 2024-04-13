package com.personia.integrations

import com.personia.dto.Token
import com.personia.plugins.configureRouting
import com.personia.utils.randomString
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class HierarchyTest {

    @Test
    fun testHierarchy() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                gson()
            }
        }
        assertNotNull(client)
        environment {
            config = ApplicationConfig("application-test.conf")
        }
        application {
            configureRouting(environment.config)
        }
        val responseLogin = authenticateClient(client)
        assertNotNull(responseLogin)
        val accessToken = responseLogin.body<Token?>()
        assertNotNull(accessToken)

        // Testing hierarchy endpoint
        val responseHierarchy = client.post("/hierarchy") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${accessToken.token}")
            setBody(mapOf("Nick" to "Sophie", "Sophie" to "Jonas", "Pete" to "Nick", "Barbara" to "Nick"))
        }
        assertEquals(HttpStatusCode.OK, responseHierarchy.status)
        assertTrue(responseHierarchy.bodyAsText().contains("Jonas"))

        // Testing supervisors
        val response = client.get("hierarchy/Nick/supervisors") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${accessToken.token}")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Jonas"))
    }

    private suspend fun authenticateClient(client: HttpClient): HttpResponse {
        val credential = mapOf(
            "username" to randomString(10),
            "password" to randomString(10)
        )

        val responseSignUp = client.post("/signup") {
            contentType(ContentType.Application.Json)
            setBody(credential)
        }
        assertEquals(HttpStatusCode.Created, responseSignUp.status)
        assertEquals("User singed up", responseSignUp.bodyAsText())

        // Testing login endpoint
        val responseLogin = client.post("/login") {
            contentType(ContentType.Application.Json)
            setBody(credential)
        }
        assertEquals(HttpStatusCode.OK, responseLogin.status)
        return responseLogin
    }
}