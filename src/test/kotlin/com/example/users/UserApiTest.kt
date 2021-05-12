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

class UserApiTest : BaseApiTest() {

    @Test
    fun `test whether GET user returns 200 when a valid user is currently logged in`() = withTestAppBase {
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
            handleRequest(HttpMethod.Get, "api/user") {
                addHeader("Authorization", "Bearer ${userResponse.user.token}")
            }.apply {
                response shouldHaveStatus HttpStatusCode.OK
            }
        }
    }

    @Test
    fun `test whether GET user returns 401 when no valid user is currently logged in`() = withTestAppBase {
        handleRequest(HttpMethod.Get, "api/user") {
            addHeader(HttpHeaders.ContentType, "application/json")
        }.apply {
            response shouldHaveStatus HttpStatusCode.Unauthorized
        }
    }

    @Test
    fun `test whether PUT user returns 200 when posted with a valid request body`() = withTestAppBase {
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
            handleRequest(HttpMethod.Put, "api/user") {
                addHeader(HttpHeaders.ContentType, "application/json")
                addHeader("Authorization", "Bearer ${userResponse.user.token}")
            }.apply {
                response shouldHaveStatus HttpStatusCode.OK
                transaction {
                    UserDao.find {
                        UserTable.username eq testUsername
                    }.firstOrNull()?.bio.shouldNotBeEmpty()
                }
            }
        }
    }

    @Test
    fun `test whether PUT user returns 422 when posted with an invalid request body`() = withTestAppBase {
        handleRequest(HttpMethod.Post, "api/users/login") {
            addHeader(HttpHeaders.ContentType, "application/json")
            setBody(putInvalidUser)
        }.apply {
            response shouldHaveStatus HttpStatusCode.UnprocessableEntity
        }
    }
}