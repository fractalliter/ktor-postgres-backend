package com.personia.services

import io.ktor.server.config.*
import io.ktor.server.testing.*
import org.junit.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class NodeServiceTest {
    @Test
    fun getHierarchy() = testApplication {
        createClient { }
        environment {
            config = ApplicationConfig("application-test.conf")
        }
        val hierarchy = nodeService.getHierarchy()
        assertNotNull(hierarchy)
    }

    @Test
    fun findByName() = testApplication {
        createClient { }
        environment {
            config = ApplicationConfig("application-test.conf")
        }
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
    fun createHierarchy() = testApplication {
        createClient {}
        environment {
            config = ApplicationConfig("application-test.conf")
        }
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
    fun retrieveSupervisors() = testApplication {
        createClient { }
        environment {
            config = ApplicationConfig("application-test.conf")
        }
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