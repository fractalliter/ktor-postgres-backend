package com.personia

import com.personia.dao.DatabaseFactory
import com.personia.plugins.configureAuthentication
import com.personia.plugins.configureException
import com.personia.plugins.configureRouting
import com.personia.plugins.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    configureAuthentication(environment.config)
    DatabaseFactory.init(environment.config)
    configureSerialization()
    configureRouting(environment.config)
    configureException()
}
