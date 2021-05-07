package com.example

import com.example.articles.articleModules
import com.example.core.coreModules
import com.example.core.db.AppDatabase
import io.ktor.server.netty.*
import com.example.plugins.*
import com.example.tags.tagModules
import com.example.users.userModules
import io.ktor.application.*
import org.koin.ktor.ext.koin
import org.koin.logger.SLF4JLogger

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module(testing: Boolean = false) {
    configureKoin()
    configureAuthentication()
    configureRouting()
    configureHTTP()
    configureMonitoring()
    configureSerialization()
    configureStatusPages()
    if(!testing) AppDatabase.init()
}