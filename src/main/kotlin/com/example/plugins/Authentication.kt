package com.example.plugins

import com.example.core.JwtConfig
import com.example.core.toUUID
import com.example.users.UserCache
import com.example.users.toUser
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*

fun Application.configureAuthentication() {

    install(Authentication) {
        jwt {
            verifier(JwtConfig.verifier)
            realm = "ktor.io"
            validate {
                it.payload.getClaim("id").asString()?.let { userId ->
                    UserCache.get(userId.toUUID())?.toUser()
                }
            }
        }
    }
}

