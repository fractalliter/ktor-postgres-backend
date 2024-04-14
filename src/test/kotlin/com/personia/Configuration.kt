package com.personia

import com.personia.plugins.configureRouting
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.gson.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import kotlin.test.assertNotNull

fun ApplicationTestBuilder.configIntegrationTestApp(): HttpClient {
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
    return client
}

fun ApplicationTestBuilder.configureUnitTestApp(){
    createClient {  }
    environment {
        config = ApplicationConfig("application-test.conf")
    }
}