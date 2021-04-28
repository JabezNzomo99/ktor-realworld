package com.example.articles

import com.example.users.User

class Controller(private val repository: Repository) {

    suspend fun post(loggedInUser: User, postArticle: PostArticle): Article {
        postArticle.validate()
        return repository.create(loggedInUser, postArticle)
    }

    suspend fun favorite(loggedInUser: User, slug: String): Article {
        return repository.favorite(loggedInUser, slug)
    }

    suspend fun unfavorite(loggedInUser: User, slug: String): Article {
        return repository.unfavorite(loggedInUser, slug)
    }

    suspend fun put(loggedInUser: User, slug: String, putArticle: PutArticle): Article {
        return repository.update(loggedInUser, slug, putArticle)
    }

    suspend fun delete(loggedInUser: User, slug: String) {
        repository.delete(loggedInUser, slug)
    }

    suspend fun getAll(loggedInUser: User?, tag: String?, author: String?, favorite: String?): List<Article> {
        return if (!tag.isNullOrEmpty()) {
            repository.getAllArticlesByTag(loggedInUser, tag)
        } else if (!author.isNullOrEmpty()) {
            repository.getAllArticlesByAuthor(loggedInUser, author)
        } else if (!favorite.isNullOrEmpty()) {
            repository.getAllArticlesByFavorite(loggedInUser, favorite)
        } else {
            repository.getAll(loggedInUser)
        }
    }

    suspend fun comment(loggedInUser: User, postComment: PostComment, slug: String): Comment {
        postComment.validate()
        return repository.addComment(loggedInUser, postComment, slug)
    }

    suspend fun getAllComments(loggedInUser: User?, slug: String): List<Comment> {
        return repository.getAllComments(loggedInUser, slug)
    }

    suspend fun deleteComment(loggedInUser: User, slug: String, commentId: String) {
        repository.deleteComment(loggedInUser, slug, commentId)
    }
}