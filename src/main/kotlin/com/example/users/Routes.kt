package com.example.users

import com.example.core.MissingRequestBodyException
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Route.users() {

    val controller: Controller by inject()

    route("users") {
        post {
            val postUser = call.receiveOrNull<PostUser>()
                ?: throw MissingRequestBodyException("User body is required!")

            call.respond(HttpStatusCode.OK, message = controller.register(postUser))
        }

        post("login") {
            val postLogin = call.receiveOrNull<PostLogin>()
                ?: throw MissingRequestBodyException("User body is required!")

            call.respond(HttpStatusCode.OK, message = controller.login(postLogin))
        }
    }

    route("user") {
        authenticate {
            get {
                call.respond(HttpStatusCode.OK, message = mapOf("user" to this.call.principal<User>()))
            }

            put {
                val updateUser = call.receiveOrNull<PutUser>()
                    ?: throw MissingRequestBodyException("User body is required!")

                call.respond(HttpStatusCode.OK, message =
                controller.put(loggedInUser = this.call.principal()!!, putUser = updateUser))
            }
        }
    }

    route("profiles") {
        authenticate(optional = true) {
            get("/{username}") {
                val username = call.parameters["username"]
                    ?: throw MissingRequestBodyException("Username is required!")
                call.respond(HttpStatusCode.OK,
                    message = mapOf("profile" to controller.getProfile(call.principal(),username)))
            }
        }

        authenticate {
            post("/{username}/follow") {
                val username = call.parameters["username"]
                    ?: throw MissingRequestBodyException("Username is required!")
                call.respond(HttpStatusCode.OK,
                    message = mapOf("profile" to controller.followUser(call.principal()!!,username)))
            }

            post("/{username}/unfollow") {
                val username = call.parameters["username"]
                    ?: throw MissingRequestBodyException("Username is required!")
                call.respond(HttpStatusCode.OK,
                    message = mapOf("profile" to controller.unfollowUser(call.principal()!!,username)))
            }
        }
    }
}