package com.example.plugins

import com.example.articles.articles
import com.example.tags.tags
import com.example.users.users
import io.ktor.application.*
import io.ktor.routing.*

fun Application.configureRouting() {

    routing {
        route("api") {
            users()
            articles()
            tags()
        }
    }
}