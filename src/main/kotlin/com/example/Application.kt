package com.example

import com.example.core.db.AppDatabase
import io.ktor.server.netty.*
import com.example.plugins.*
import io.ktor.application.*

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module(testing: Boolean = false) {
    configureKoin()
    configureAuthentication()
    configureRouting()
    configureHTTP()
    configureMonitoring()
    configureSerialization()
    configureStatusPages()
    AppDatabase.init()
}