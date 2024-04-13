package com.personia.services

import io.ktor.server.config.*
import io.ktor.server.testing.*
import org.junit.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
class NodeServiceTest {

    private fun nodeServiceTest(test: suspend () -> Unit) = testApplication {
        createClient {  }
        environment {
            config = ApplicationConfig("application-test.conf")
        }
        test()
    }
    @Test
    fun getHierarchy() = nodeServiceTest {
        val hierarchy = nodeService.getHierarchy()
        assertNotNull(hierarchy)
    }

    @Test
    fun findByName() = nodeServiceTest {
        nodeService.createHierarchy(
            mapOf(
                "Pete" to "Nick",
                "Barbara" to "Nick",
                "Nick" to "Sophie",
                "Sophie" to "Jonas"
            )
        )

        val node = nodeService.findByName("Pete")
        assertNotNull(node)
    }

    @Test
    fun createHierarchy() = nodeServiceTest {
        val response = nodeService.createHierarchy(
            mapOf(
                "Pete" to "Nick",
                "Barbara" to "Nick",
                "Nick" to "Sophie",
                "Sophie" to "Jonas"
            )
        )
        assertNotNull(response)
    }

    @Test
    fun retrieveSupervisors() = nodeServiceTest {
        nodeService.createHierarchy(
            mapOf(
                "Pete" to "Nick",
                "Barbara" to "Nick",
                "Nick" to "Sophie",
                "Sophie" to "Jonas"
            )
        )
        val supervisors = nodeService.retrieveSupervisors("Pete", 1)
        assertNotNull(supervisors)
        assertNotEquals(supervisors.size, 0)
    }
}