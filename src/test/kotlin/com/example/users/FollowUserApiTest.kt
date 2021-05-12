package com.example.users

import com.example.BaseApiTest
import com.example.core.encrypt
import com.example.getRandomEmail
import com.example.getRandomUserName
import com.example.withTestAppBase
import com.google.gson.Gson
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.string.shouldNotBeEmpty
import io.ktor.http.*
import io.ktor.server.testing.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test

class FollowUserApiTest : BaseApiTest() {

    @Test
    fun `test whether follow user returns 401 when auth token is missing`() = withTestAppBase {
        handleRequest(HttpMethod.Put, "api/${getRandomUserName()}/follow") {
            addHeader(HttpHeaders.ContentType, "application/json")
        }.apply {
            response shouldHaveStatus HttpStatusCode.Unauthorized
        }
    }

    @Test
    fun `test whether follow user returns 200 when posted with valid parameters`() = withTestAppBase {
        val testUsername = getRandomUserName()
        val testEmail = getRandomEmail()
        val testUsername2 = getRandomUserName()
        val testEmail2 = getRandomEmail()

        val currentUser: UserDao = transaction {
            UserDao.new {
                email = testEmail
                username = testUsername
                password = "test".encrypt()
            }
        }

        val followingUser: UserDao = transaction {
            UserDao.new {
                email = testEmail2
                username = testUsername2
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
            handleRequest(HttpMethod.Put, "api/${testUsername2}/follow") {
                addHeader(HttpHeaders.ContentType, "application/json")
                addHeader("Authorization", "Bearer ${userResponse.user.token}")
            }.apply {
                response shouldHaveStatus HttpStatusCode.OK
                transaction {
                    UserFollowingDao.find {
                        (UserFollowingTable.userId eq currentUser.uid) and
                            (UserFollowingTable.followingId eq followingUser.uid)
                    }
                }.shouldNotBeEmpty()
            }
        }
    }

    @Test
    fun `test whether follow user returns 404 when username to follow does not exist`() = withTestAppBase {
        val testUsername = getRandomUserName()
        val testEmail = getRandomEmail()

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
            handleRequest(HttpMethod.Put, "api/${getRandomUserName()}/follow") {
                addHeader(HttpHeaders.ContentType, "application/json")
                addHeader("Authorization", "Bearer ${userResponse.user.token}")
            }.apply {
                response shouldHaveStatus HttpStatusCode.NotFound
            }
        }
    }

    @Test
    fun `test whether unfollow user returns 401 when auth token is missing`() = withTestAppBase {
        handleRequest(HttpMethod.Put, "api/${getRandomUserName()}/unfollow") {
            addHeader(HttpHeaders.ContentType, "application/json")
        }.apply {
            response shouldHaveStatus HttpStatusCode.Unauthorized
        }
    }

    @Test
    fun `test whether unfollow user returns 200 when posted with valid parameters`() = withTestAppBase {
        val testUsername = getRandomUserName()
        val testEmail = getRandomEmail()
        val testUsername2 = getRandomUserName()
        val testEmail2 = getRandomEmail()

        // Create current user
        val currentUser: UserDao = transaction {
            UserDao.new {
                email = testEmail
                username = testUsername
                password = "test".encrypt()
            }
        }

        //Create user to follow
        val followingUser: UserDao = transaction {
            UserDao.new {
                email = testEmail2
                username = testUsername2
                password = "test".encrypt()
            }
        }

        // Create following rshp btwn current user and following user
        transaction {
            UserFollowingDao.new {
                this.userId = currentUser
                this.followingId = followingUser
            }
        }

        handleRequest(HttpMethod.Post, "api/users/login") {
            addHeader(HttpHeaders.ContentType, "application/json")
            setBody(postValidLogin.replace("$", testEmail))
        }.apply {
            response shouldHaveStatus HttpStatusCode.OK
            val userResponse = Gson().fromJson(response.content, UserResponse::class.java)
            userResponse.user.token.shouldNotBeEmpty()
            handleRequest(HttpMethod.Put, "api/${testUsername2}/unfollow") {
                addHeader(HttpHeaders.ContentType, "application/json")
                addHeader("Authorization", "Bearer ${userResponse.user.token}")
            }.apply {
                response shouldHaveStatus HttpStatusCode.OK
                transaction {
                    UserFollowingDao.find {
                        (UserFollowingTable.userId eq currentUser.uid) and
                            (UserFollowingTable.followingId eq followingUser.uid)
                    }
                }.shouldBeEmpty()
            }
        }
    }

    @Test
    fun `test whether ufollow user returns 404 when username to unfollow does not exist`() = withTestAppBase {
        val testUsername = getRandomUserName()
        val testEmail = getRandomEmail()

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
            handleRequest(HttpMethod.Put, "api/${getRandomUserName()}/unfollow") {
                addHeader(HttpHeaders.ContentType, "application/json")
                addHeader("Authorization", "Bearer ${userResponse.user.token}")
            }.apply {
                response shouldHaveStatus HttpStatusCode.NotFound
            }
        }
    }
}