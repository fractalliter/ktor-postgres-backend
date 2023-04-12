package com.personia.routes

import com.google.gson.Gson
import com.personia.services.NodeService

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private val applicationJson = ContentType("application", "json")
private val gson = Gson()

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
            val json: String = gson.toJson(response)
            call.respondText(text = json,contentType = applicationJson)
        }
        post {
            val hierarchy = call.receive<Map<String, String>>()
            val response = nodeService.createHierarchy(hierarchy)
            val json = gson.toJson(response)
            call.respondText(text = json,contentType = applicationJson)
        }
    }
}