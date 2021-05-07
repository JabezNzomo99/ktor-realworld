package com.example

import io.ktor.server.testing.*

fun withTestAppBase(test: TestApplicationEngine.() -> Unit) {
    withTestApplication(
        {
            module(testing = true)
        },
        test
    )
}