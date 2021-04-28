package com.example.plugins

import com.example.articles.articleModules
import com.example.core.coreModules
import com.example.tags.tagModules
import com.example.users.userModules
import io.ktor.application.*
import org.koin.ktor.ext.Koin
import org.koin.logger.SLF4JLogger

fun Application.configureKoin(){
    install(Koin){
        SLF4JLogger()
        modules(coreModules)
        modules(userModules)
        modules(articleModules)
        modules(tagModules)
    }
}