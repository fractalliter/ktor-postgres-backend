package com.personia.plugins

import com.personia.routes.authRouting
import com.personia.routes.hierarchyRouting
import com.personia.services.nodeService
import com.personia.services.userService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.config.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(config: ApplicationConfig) {

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        authenticate("auth-jwt") {
            hierarchyRouting(nodeService)
        }
        authRouting(config, userService)
    }
}
