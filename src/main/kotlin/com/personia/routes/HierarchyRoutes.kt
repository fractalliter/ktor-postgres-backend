package com.personia.routes

import com.personia.services.NodeService

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.hierarchyRouting(nodeService: NodeService) {
    route("hierarchy") {
        get {
            call.respond(nodeService.getHierarchy())
        }
        get("{name?}") {
            val name = call.parameters["name"] ?: return@get call.respondText(
                "Missing name",
                status = HttpStatusCode.BadRequest
            )
            val employee =
                nodeService.findByName(name) ?: return@get call.respondText(
                    "No employee with name $name",
                    status = HttpStatusCode.NotFound
                )
            call.respond(employee)
        }

        get("{name?}/supervisors") {
            val name = call.parameters["name"] ?: return@get call.respondText(
                "Missing name",
                status = HttpStatusCode.BadRequest
            )
            val level = call.request.queryParameters["level"]?.toInt() ?: 2

            val response = nodeService.retrieveSupervisors(name, level)
            call.respond(response)
        }
        post {
            val hierarchy = call.receive<Map<String, String>>()
            val response = nodeService.createHierarchy(hierarchy)
            call.respond(response)
        }
    }
}