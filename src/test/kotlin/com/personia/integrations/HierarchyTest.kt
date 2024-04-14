package com.personia.integrations

import com.personia.configIntegrationTestApp
import com.personia.dto.Token
import com.personia.utils.randomString
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HierarchyTest {
    @Test
    fun testHierarchy() = testApplication {
        val client = configIntegrationTestApp()
        val token = authenticate(client)
        val responseHierarchy = client.post("/hierarchy") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
            setBody(mapOf("Nick" to "Sophie", "Sophie" to "Jonas", "Pete" to "Nick", "Barbara" to "Nick"))
        }
        assertEquals(HttpStatusCode.OK, responseHierarchy.status)
        assertTrue(responseHierarchy.bodyAsText().contains("Jonas"))
    }

    @Test
    fun testSupervisor() = testApplication {
        val client = configIntegrationTestApp()
        val token = authenticate(client)
        val response = client.get("hierarchy/Nick/supervisors") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Jonas"))
    }

    private suspend fun authenticate(client: HttpClient): String?{
        val credential = mapOf(
            "username" to randomString(10),
            "password" to randomString(10)
        )
        client.post("/signup") {
            contentType(ContentType.Application.Json)
            setBody(credential)
        }
        val responseLogin = client.post("/login") {
            contentType(ContentType.Application.Json)
            setBody(credential)
        }
        return responseLogin.body<Token?>()?.token

    }
}