package com.example.users

import com.example.BaseApiTest
import com.example.withTestAppBase
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.ktor.http.*
import io.ktor.server.testing.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test

class RegisterUserApiTest : BaseApiTest() {

    @Test
    fun `test whether POST user returns 422 when posted with an invalid request body`() = withTestAppBase {
        handleRequest(HttpMethod.Post, "api/users") {
            addHeader(HttpHeaders.ContentType, "application/json")
            setBody(postInvalidUser)
        }.apply {
            response shouldHaveStatus HttpStatusCode.UnprocessableEntity
        }
    }

    @Test
    fun `test whether POST user returns 200 when posted with an valid request body and saves the user`() = withTestAppBase {
        handleRequest(HttpMethod.Post, "api/users") {
            addHeader(HttpHeaders.ContentType, "application/json")
            setBody(postUser)
        }.apply {
            response shouldHaveStatus HttpStatusCode.OK
        }

        transaction {
            UserDao.find {
                (UserTable.username eq "test")
            }.shouldNotBeEmpty()
        }
    }
}