package com.example.users

import org.koin.dsl.module

private val repositoryModule = module {
    single {
        Repository()
    }
}

private val controllerModule = module {
    single {
        Controller(repository = get())
    }
}

val userModules = listOf(repositoryModule, controllerModule)