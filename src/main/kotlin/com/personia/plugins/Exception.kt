package com.personia.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import java.sql.SQLException

data class ExceptionTransform(val message: String?, val code: Int)

fun Application.configureException() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->

            val log = call.application.environment.log
            when (cause) {
                is AssertionError -> {
                    log.info(cause.message)
                    call.response.status(HttpStatusCode.BadRequest)
                    call.respond(
                        ExceptionTransform(cause.message, HttpStatusCode.BadRequest.value)
                    )
                }

                is SecurityException -> {
                    log.info(cause.message)
                    call.response.status(HttpStatusCode.Forbidden)
                    call.respond(
                        ExceptionTransform("You are a bad guy", HttpStatusCode.Forbidden.value),
                    )
                }

                is SQLException -> {
                    log.info(cause.message)
                    call.response.status(HttpStatusCode.BadRequest)
                    call.respond(
                        ExceptionTransform(
                            "duplicate key value violates unique constraint.",
                            HttpStatusCode.BadRequest.value
                        ),
                    )
                }

                is NotFoundException -> {
                    log.error(cause.message)
                    call.response.status(HttpStatusCode.NotFound)
                    call.respond(
                       ExceptionTransform(cause.message, HttpStatusCode.NotFound.value),
                    )
                }

                is Exception -> {
                    log.error(cause.message)
                    call.response.status(HttpStatusCode.InternalServerError)
                    call.respond(
                        ExceptionTransform(cause.message, HttpStatusCode.InternalServerError.value),
                    )
                }
            }
        }
    }
}