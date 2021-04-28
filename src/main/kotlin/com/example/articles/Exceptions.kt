package com.example.articles

data class ArticleNotFoundException(override val message: String?) : RuntimeException()
data class CommentDoesNotExistException(override val message: String?) : RuntimeException()

