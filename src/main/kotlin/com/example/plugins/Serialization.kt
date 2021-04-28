package com.example.plugins

import io.ktor.gson.*
import io.ktor.features.*
import io.ktor.application.*

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }
}
