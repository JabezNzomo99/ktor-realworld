package com.example.users

import com.example.BaseApiTest
import com.example.core.encrypt
import com.example.withTestAppBase
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.matchers.string.shouldNotBeEmpty
import io.ktor.http.*
import io.ktor.server.testing.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test
import kotlin.random.Random

class ProfileApiTest : BaseApiTest() {

    @Test
    fun `test whether GET user profile returns 200 when user with the requested username exists`() = withTestAppBase {
        val testUsername = "test${Random.nextInt()}"
        val testEmail = "test${Random.nextInt()}@gmail.com"
        transaction {
            UserDao.new {
                email = testEmail
                username = testUsername
                password = "test".encrypt()
            }
        }
        handleRequest(HttpMethod.Get, "api/profiles/${testUsername}").apply {
            response shouldHaveStatus HttpStatusCode.OK
            response.content.shouldNotBeEmpty()
        }
    }

    @Test
    fun `test whether GET user profile returns 404 when user with the requested username does not exists`() = withTestAppBase {
        handleRequest(HttpMethod.Get, "api/profiles/${Random.nextInt()}").apply {
            response shouldHaveStatus HttpStatusCode.NotFound
        }
    }
}