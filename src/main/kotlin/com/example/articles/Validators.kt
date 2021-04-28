package com.example.articles

import org.valiktor.ConstraintViolationException
import org.valiktor.functions.isNotEmpty
import org.valiktor.functions.isNotNull
import org.valiktor.functions.validate
import kotlin.jvm.Throws

@Throws(ConstraintViolationException::class)
fun PostArticle.validate() {
    org.valiktor.validate(this) {
        validate(PostArticle::article).isNotNull()
        validate(PostArticle::article).validate {
            validate(CreateArticle::title).apply {
                isNotNull()
                isNotEmpty()
            }
            validate(CreateArticle::description).apply {
                isNotNull()
                isNotEmpty()
            }
            validate(CreateArticle::body).apply {
                isNotNull()
                isNotEmpty()
            }
        }
    }
}

@Throws(ConstraintViolationException::class)
fun PostComment.validate() {
    org.valiktor.validate(this) {
        validate(PostComment::comment).isNotNull()
        validate(PostComment::comment).validate {
            validate(AddComment::body).apply {
                isNotNull()
                isNotEmpty()
            }
        }
    }
}