package com.example.users

import com.example.BaseApiTest
import com.example.core.encrypt
import com.example.withTestAppBase
import com.google.gson.Gson
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.matchers.string.shouldNotBeEmpty
import io.ktor.http.*
import io.ktor.server.testing.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test
import kotlin.random.Random

class LoginUserApiTest : BaseApiTest() {

    @Test
    fun `test whether POST login returns 422 when posted with an invalid request body`() = withTestAppBase {
        handleRequest(HttpMethod.Post, "api/users/login") {
            addHeader(HttpHeaders.ContentType, "application/json")
            setBody(postInvalidLogin)
        }.apply {
            response shouldHaveStatus HttpStatusCode.UnprocessableEntity
        }
    }


    @Test
    fun `test whether POST login returns 200 when posted with valid user credentials`() = withTestAppBase {
        val testUsername = "test${Random.nextInt()}"
        val testEmail = "test${Random.nextInt()}@gmail.com"
        transaction {
            UserDao.new {
                email = testEmail
                username = testUsername
                password = "test".encrypt()
            }
        }
        handleRequest(HttpMethod.Post, "api/users/login") {
            addHeader(HttpHeaders.ContentType, "application/json")
            setBody(postValidLogin.replace("$", testEmail))
        }.apply {
            response shouldHaveStatus HttpStatusCode.OK
            val userResponse = Gson().fromJson(response.content, UserResponse::class.java)
            userResponse.user.token.shouldNotBeEmpty()
        }
    }

    @Test
    fun `test whether POST login returns 401 when posted with invalid user credentials`() = withTestAppBase {
        val testUsername = "test${Random.nextInt()}"
        val testEmail = "test${Random.nextInt()}@gmail.com"
        transaction {
            UserDao.new {
                email = testEmail
                username = testUsername
                password = "test".encrypt()
            }
        }
        handleRequest(HttpMethod.Post, "api/users/login") {
            addHeader(HttpHeaders.ContentType, "application/json")
            setBody(postInvalidCredentialsLogin.replace("$", testEmail))
        }.apply {
            response shouldHaveStatus HttpStatusCode.Unauthorized
        }
    }

    @Test
    fun `test whether POST login returns 404 when posted with a non-existent user`() = withTestAppBase {
        handleRequest(HttpMethod.Post, "api/users/login") {
            addHeader(HttpHeaders.ContentType, "application/json")
            setBody(postNonExistentUserLogin)
        }.apply {
            response shouldHaveStatus HttpStatusCode.Unauthorized
        }
    }
}