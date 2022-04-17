package com.personia.plugins

import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import java.sql.SQLException

data class ExceptionTransform(val message: String?, val code: Int)

fun transformException(message: String?, code: Int): String {
    val gson = Gson()
    val exception = ExceptionTransform(message, code)
    return gson.toJson(exception)
}

fun Application.configureException() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            when(cause) {
                is AssertionError -> {
                    call.application.environment.log.info(cause.message)
                    call.respondText(
                        text = transformException(cause.message, HttpStatusCode.BadRequest.value),
                        status = HttpStatusCode.BadRequest,
                        contentType = ContentType("application","json")
                    )
                }
                is SecurityException -> {
                    call.application.environment.log.info(cause.message)
                    call.respondText(
                        text = transformException("You are a bad guy", HttpStatusCode.Forbidden.value),
                        status = HttpStatusCode.Forbidden,
                        contentType = ContentType("application","json")
                    )
                }
                is SQLException -> {
                    call.application.environment.log.info(cause.message)
                    call.respondText(
                        text = transformException("duplicate key value violates unique constraint.", HttpStatusCode.BadRequest.value),
                        status = HttpStatusCode.Forbidden,
                        contentType = ContentType("application","json")
                    )
                }
                is NotFoundException -> {
                    call.application.environment.log.info(cause.message)
                    call.respondText(
                        text = transformException(cause.message, HttpStatusCode.NotFound.value),
                        status = HttpStatusCode.NotFound,
                        contentType = ContentType("application","json")
                    )
                }
                is Exception ->{
                    call.application.environment.log.error(cause.message)
                    call.respondText(
                        text = transformException(cause.message, HttpStatusCode.InternalServerError.value),
                        status = HttpStatusCode.InternalServerError,
                        contentType = ContentType("application","json")
                    )
                }
            }
        }
    }
}