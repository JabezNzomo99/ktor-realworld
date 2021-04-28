package com.example.articles

import org.koin.dsl.module

private val repositoryModule = module {
    single {
        Repository(tagsRepository = get())
    }
}

private val controllerModule = module {
    single {
        Controller(repository = get())
    }
}

val articleModules = listOf(repositoryModule, controllerModule)