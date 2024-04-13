package com.personia.plugins

import com.personia.dto.Exception
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import java.sql.SQLException

fun Application.configureException() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->

            val log = call.application.environment.log
            when (cause) {
                is AssertionError -> {
                    log.info(cause.message)
                    call.response.status(HttpStatusCode.BadRequest)
                    call.respond(
                        Exception(cause.message, HttpStatusCode.BadRequest.value)
                    )
                }

                is SecurityException -> {
                    log.info(cause.message)
                    call.response.status(HttpStatusCode.Forbidden)
                    call.respond(
                        Exception("You are a bad guy", HttpStatusCode.Forbidden.value),
                    )
                }

                is SQLException -> {
                    log.info(cause.message)
                    call.response.status(HttpStatusCode.BadRequest)
                    call.respond(
                        Exception(
                            "duplicate key value violates unique constraint.",
                            HttpStatusCode.BadRequest.value
                        ),
                    )
                }

                is NotFoundException -> {
                    log.error(cause.message)
                    call.response.status(HttpStatusCode.NotFound)
                    call.respond(
                        Exception(cause.message, HttpStatusCode.NotFound.value),
                    )
                }

                is java.lang.Exception -> {
                    log.error(cause.message)
                    call.response.status(HttpStatusCode.InternalServerError)
                    call.respond(
                        Exception(cause.message, HttpStatusCode.InternalServerError.value),
                    )
                }
            }
        }
    }
}