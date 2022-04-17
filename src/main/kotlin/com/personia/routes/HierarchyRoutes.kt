package com.personia.routes

import com.google.gson.Gson
import com.personia.services.nodeService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.hierarchyRouting() {
    route("hierarchy") {
        get {
            call.respond(nodeService.findAll())
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
            val gson = Gson()
            val json: String = gson.toJson(response)
            call.respondText(
                text=json,
                contentType = ContentType("application","json")
            )
        }
        post {
            val hierarchy = call.receive<Map<String, String>>()
            val response = nodeService.createHierarchy(hierarchy)
            val gson = Gson()
            val json = gson.toJson(response)
            call.respondText(text=json, contentType = ContentType("application","json"))
        }
    }
}