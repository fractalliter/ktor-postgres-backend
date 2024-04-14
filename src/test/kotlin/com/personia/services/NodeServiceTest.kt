package com.personia.services

import com.personia.configureUnitTestApp
import io.ktor.server.testing.*
import org.junit.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class NodeServiceTest {

    @Test
    fun getHierarchy() = testApplication {
        configureUnitTestApp()
        val hierarchy = nodeService.getHierarchy()
        assertNotNull(hierarchy)
    }

    @Test
    fun findByName() = testApplication {
        configureUnitTestApp()
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
        configureUnitTestApp()
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
        configureUnitTestApp()
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