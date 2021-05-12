package com.example

import io.ktor.server.testing.*
import kotlin.random.Random

fun withTestAppBase(test: TestApplicationEngine.() -> Unit) {
    withTestApplication(
        {
            module(testing = true)
        },
        test
    )
}

fun getRandomUserName() = "test${Random.nextInt()}"

fun getRandomEmail() = "test${Random.nextInt()}@gmail.com"