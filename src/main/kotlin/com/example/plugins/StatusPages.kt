package com.example.plugins

import com.example.articles.ArticleNotFoundException
import com.example.articles.CommentDoesNotExistException
import com.example.core.InvalidUUIDException
import com.example.core.MissingRequestBodyException
import com.example.core.models.ResponseErrors
import com.example.users.AuthenticationException
import com.example.users.AuthorizationException
import com.example.users.UserNotFoundException
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.valiktor.ConstraintViolationException

fun Application.configureStatusPages() {
    install(StatusPages) {

        exception<ConstraintViolationException> { exception ->
            val violations =
                exception.constraintViolations.map { violation -> "${violation.property}:${violation.constraint.name}" }
            call.respond(
                status = HttpStatusCode.UnprocessableEntity,
                message = ResponseErrors(ResponseErrors.Errors(violations.toList()))
            )
        }

        exception<AuthenticationException> { exception ->
            call.respond(
                status = HttpStatusCode.Unauthorized,
                message = ResponseErrors(ResponseErrors.Errors(listOf(exception.message)))
            )
        }

        exception<AuthorizationException> { exception ->
            call.respond(
                status = HttpStatusCode.Unauthorized,
                message = ResponseErrors(ResponseErrors.Errors(listOf(exception.message)))
            )
        }

        exception<UserNotFoundException> { exception ->
            call.respond(
                status = HttpStatusCode.NotFound,
                message = ResponseErrors(ResponseErrors.Errors(listOf(exception.message)))
            )
        }

        exception<MissingRequestBodyException> { exception ->
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = ResponseErrors(ResponseErrors.Errors(listOf(exception.message)))
            )
        }

        exception<InvalidUUIDException> { exception ->
            call.respond(
                status = HttpStatusCode.UnprocessableEntity,
                message = ResponseErrors(ResponseErrors.Errors(listOf(exception.message)))
            )
        }

        exception<ArticleNotFoundException> { exception ->
            call.respond(
                status = HttpStatusCode.NotFound,
                message = ResponseErrors(ResponseErrors.Errors(listOf(exception.message)))
            )
        }

        exception<CommentDoesNotExistException> { exception ->
            call.respond(
                status = HttpStatusCode.NotFound,
                message = ResponseErrors(ResponseErrors.Errors(listOf(exception.message)))
            )
        }

        exception<ExposedSQLException> { exception ->
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = ResponseErrors(ResponseErrors.Errors(listOf(exception.message)))
            )
        }
    }
}