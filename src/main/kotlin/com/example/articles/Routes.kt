package com.example.articles

import com.example.core.MissingRequestBodyException
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Route.articles() {

    val controller: Controller by inject()

    route("articles") {
        authenticate(optional = true) {
            get {
                val articles = controller.getAll(
                    loggedInUser = call.principal(),
                    tag = call.parameters["tag"],
                    author = call.parameters["author"],
                    favorite = call.parameters["favorite"]
                )
                call.respond(HttpStatusCode.OK,
                    message = mapOf("articles" to articles, "articlesCount" to articles.count()))
            }
        }

        authenticate {
            post {
                val postArticle = call.receiveOrNull<PostArticle>()
                    ?: throw MissingRequestBodyException("Post Article Body is required!")

                call.respond(HttpStatusCode.Created,
                    message = mapOf("article" to controller.post(call.principal()!!, postArticle)))
            }

            post("/{slug}/comments") {
                val slug = call.parameters["slug"] ?: throw MissingRequestBodyException("Article slug is required!")
                val addComment = call.receiveOrNull<PostComment>()
                    ?: throw MissingRequestBodyException("Comment is required!")
                call.respond(HttpStatusCode.Created,
                    message = mapOf("comment" to controller.comment(call.principal()!!, addComment, slug)))
            }

            get("/{slug}/comments") {
                val slug = call.parameters["slug"] ?: throw MissingRequestBodyException("Article slug is required!")
                call.respond(HttpStatusCode.OK,
                    message = mapOf("comments" to controller.getAllComments(call.principal(), slug)))
            }

            delete("/{slug}/comments/{commentId}") {
                val slug = call.parameters["slug"] ?: throw MissingRequestBodyException("Article slug is required!")
                val commentId = call.parameters["commentId"]
                    ?: throw MissingRequestBodyException("Article slug is required!")
                controller.deleteComment(call.principal()!!, slug, commentId)
                call.respond(HttpStatusCode.OK, mapOf("Response" to "Comment Deleted"))
            }

            post("/{slug}/favorite") {
                val slug = call.parameters["slug"] ?: throw MissingRequestBodyException("Article slug is required!")
                call.respond(HttpStatusCode.Created,
                    message = mapOf("article" to controller.favorite(call.principal()!!, slug)))
            }

            delete("/{slug}/favorite") {
                val slug = call.parameters["slug"] ?: throw MissingRequestBodyException("Article slug is required!")
                call.respond(HttpStatusCode.Created,
                    message = mapOf("article" to controller.unfavorite(call.principal()!!, slug)))
            }

            put("/{slug}") {
                val slug = call.parameters["slug"] ?: throw MissingRequestBodyException("Article slug is required!")
                val putArticle = call.receiveOrNull<PutArticle>()
                    ?: throw MissingRequestBodyException("Article Body is required!")

                call.respond(HttpStatusCode.Created,
                    message = mapOf("article" to controller.put(call.principal()!!, slug, putArticle)))
            }

            delete("/{slug}") {
                val slug = call.parameters["slug"] ?: throw MissingRequestBodyException("Article slug is required!")

                controller.delete(call.principal()!!, slug)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}