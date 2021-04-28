package com.example.tags

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

val tagModules = listOf(repositoryModule, controllerModule)