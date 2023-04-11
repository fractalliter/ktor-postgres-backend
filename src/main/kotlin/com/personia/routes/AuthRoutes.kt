package com.personia.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.personia.models.User
import com.personia.services.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.authRouting(config: ApplicationConfig, userService: UserService) {
    val secret = config.property("jwt.secret").getString()
    val issuer = config.property("jwt.issuer").getString()
    val audience = config.property("jwt.audience").getString()
    val expiration: Int = config.property("jwt.expiration").getString().toInt()

    post("/login") {
        val user = call.receive<User>()
        val userStatus = userService.loginUser(user.username, user.password)
        if (userStatus) {
            val token = JWT.create()
                .withAudience(audience)
                .withIssuer(issuer)
                .withClaim("username", user.username)
                .withExpiresAt(Date(System.currentTimeMillis() + expiration))
                .sign(Algorithm.HMAC256(secret))
            call.respond(hashMapOf("token" to token))
        } else call.respond(HttpStatusCode.Unauthorized, "username/password is wrong")
    }


    post("/signup") {
        val user = call.receive<User>()
        userService.createUser(user)
        call.respond(HttpStatusCode.Created, "User singed up")
    }
}