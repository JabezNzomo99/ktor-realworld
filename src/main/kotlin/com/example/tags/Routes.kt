package com.example.tags

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Route.tags() {

    val controller: Controller by inject()

    route("tags") {
        get {
            call.respond(HttpStatusCode.OK, mapOf("tags" to controller.getAll()))
        }
    }
}