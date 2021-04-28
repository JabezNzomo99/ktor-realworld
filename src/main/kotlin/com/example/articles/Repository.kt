package com.example.articles

import com.example.core.toUUID
import com.example.users.*
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import com.example.tags.Repository as TagsRepository
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.*

class Repository(private val tagsRepository: TagsRepository) {
    suspend fun create(loggedInUser: User, postArticle: PostArticle) = newSuspendedTransaction {
        val article = ArticleDao.new {
            title = postArticle.article.title
            slug = postArticle.article.title
            body = postArticle.article.body
            description = postArticle.article.description
            tags = postArticle.article.tags?.joinToString()
            author = UserDao.find { (UserTable.email) eq loggedInUser.email }.first()
        }
        if (!postArticle.article.tags.isNullOrEmpty()) tagsRepository.add(postArticle.article.tags)
        article.toArticle()
    }

    suspend fun update(loggedInUser: User, slug: String, putArticle: PutArticle): Article =
        newSuspendedTransaction {
            val article = getArticleBySlug(slug)
            if (article.author.uid != loggedInUser.id) {
                throw AuthorizationException("You are not allowed to edit this article")
            }

            val updatedArticle = article.apply {
                if (!putArticle.article.title.isNullOrEmpty()) {
                    this.title = putArticle.article.title
                    this.slug = putArticle.article.title
                }
                if (!putArticle.article.body.isNullOrEmpty()) body = putArticle.article.body
                if (!putArticle.article.description.isNullOrEmpty()) description = putArticle.article.description
                if (!putArticle.article.tags.isNullOrEmpty()) {
                    tags = putArticle.article.tags.joinToString()
                }
            }
            if (!putArticle.article.tags.isNullOrEmpty()) tagsRepository.add(putArticle.article.tags)
            updatedArticle.toArticle()
        }

    suspend fun delete(loggedInUser: User, slug: String) = newSuspendedTransaction {
        val article = ArticleDao.find {
            (ArticleTable.slug eq slug)
        }.firstOrNull() ?: throw ArticleNotFoundException("Article with title:$slug does not exist!")
        if (article.author.uid != loggedInUser.id) throw AuthorizationException("You are not allowed to edit this article")
        article.delete()
    }

    suspend fun getAll(loggedInUser: User?): List<Article> = newSuspendedTransaction {
        ArticleDao
            .all()
            .orderBy(ArticleTable.createdAt to SortOrder.DESC)
            .map {
                it.toArticle(isFavorite = checkIfArticleIsFavorite(loggedInUser?.id, it.uid),
                    favoritesCount = getArticleFavoritesCount(it))
            }
    }

    suspend fun getAllArticlesByTag(loggedInUser: User?, tag: String): List<Article> = newSuspendedTransaction {
        ArticleDao
            .find {
                ArticleTable.tags like "%$tag%"
            }
            .orderBy(ArticleTable.createdAt to SortOrder.DESC)
            .map {
                if (loggedInUser != null) {
                    val currentUser = UserDao.find { (UserTable.email) eq loggedInUser.email }.first()
                    it.toArticle(
                        isFavorite = checkIfArticleIsFavorite(it.author.uid, it.uid),
                        favoritesCount = getArticleFavoritesCount(it),
                        isFollowing = checkIfUserFollowsAuthor(currentUser, it.author))
                } else {
                    it.toArticle(isFavorite = checkIfArticleIsFavorite(loggedInUser?.id, it.uid),
                        favoritesCount = getArticleFavoritesCount(it))
                }
            }
    }

    suspend fun getAllArticlesByAuthor(loggedInUser: User?, authorName: String): List<Article> = newSuspendedTransaction {
        val author = UserDao.find {
            (UserTable.username eq authorName)
        }.firstOrNull()
        ArticleDao
            .find {
                ArticleTable.authorId eq author?.uid
            }
            .orderBy(ArticleTable.createdAt to SortOrder.DESC)
            .map {
                if (loggedInUser != null && author != null) {
                    val currentUser = UserDao.find { (UserTable.email) eq loggedInUser.email }.first()
                    it.toArticle(
                        isFavorite = checkIfArticleIsFavorite(author.uid, it.uid),
                        favoritesCount = getArticleFavoritesCount(it),
                        isFollowing = checkIfUserFollowsAuthor(currentUser, author))
                } else {
                    it.toArticle(isFavorite = checkIfArticleIsFavorite(author?.uid, it.uid),
                        favoritesCount = getArticleFavoritesCount(it))
                }
            }
    }

    suspend fun getAllArticlesByFavorite(loggedInUser: User?, favorite: String): List<Article> = newSuspendedTransaction {
        val user = UserDao.find { (UserTable.username) eq favorite }.firstOrNull()
        UserFavoriteDao.find {
            (UserFavoriteTable.userId eq user?.uid)
        }.map {
            if (loggedInUser != null) {
                val currentUser = UserDao.find { (UserTable.email) eq loggedInUser.email }.first()
                it.article.toArticle(
                    isFavorite = (it.user.uid == loggedInUser.id),
                    favoritesCount = getArticleFavoritesCount(it.article),
                    isFollowing = checkIfUserFollowsAuthor(currentUser, it.article.author)
                )
            } else {
                it.article.toArticle(
                    isFavorite = (it.user.uid == loggedInUser?.id),
                    favoritesCount = getArticleFavoritesCount(it.article))
            }
        }
    }

    suspend fun favorite(loggedInUser: User, slug: String): Article = newSuspendedTransaction {
        val article = getArticleBySlug(slug)
        val user = UserDao.find { (UserTable.email) eq loggedInUser.email }.first()
        UserFavoriteDao.new {
            this.user = user
            this.article = article
        }
        article.toArticle(isFavorite = true, favoritesCount = getArticleFavoritesCount(article))
    }

    suspend fun unfavorite(loggedInUser: User, slug: String): Article = newSuspendedTransaction {
        val article = getArticleBySlug(slug)
        val user = UserDao.find { (UserTable.email) eq loggedInUser.email }.first()
        UserFavoriteDao.find {
            (UserFavoriteTable.articleId eq article.uid and (UserFavoriteTable.userId eq user.uid))
        }.firstOrNull()?.delete()
        val favoritesCount = UserFavoriteDao.find {
            UserFavoriteTable.articleId eq article.uid
        }.count().toInt()
        article.toArticle(isFavorite = false, favoritesCount = favoritesCount)
    }

    private suspend fun getArticleBySlug(slug: String): ArticleDao = newSuspendedTransaction {
        ArticleDao.find {
            (ArticleTable.slug eq slug)
        }.firstOrNull() ?: throw ArticleNotFoundException("Article with title:$slug does not exist!")
    }

    private suspend fun getArticleFavoritesCount(article: ArticleDao): Int = newSuspendedTransaction {
        UserFavoriteDao.find {
            UserFavoriteTable.articleId eq article.uid
        }.count().toInt()
    }

    private suspend fun checkIfArticleIsFavorite(userId: UUID?, articleId: UUID): Boolean = newSuspendedTransaction {
        val count = UserFavoriteDao.find {
            ((UserFavoriteTable.userId eq userId) and (UserFavoriteTable.articleId eq articleId))
        }.count()
        count > 0
    }

    private suspend fun checkIfUserFollowsAuthor(user: UserDao, author: UserDao): Boolean = newSuspendedTransaction {
        !UserFollowingDao.find {
            (UserFollowingTable.userId eq user.id) and (UserFollowingTable.followingId eq author.id)
        }.empty()
    }

    suspend fun addComment(loggedInUser: User, postComment: PostComment, slug: String) = newSuspendedTransaction {
        val article = getArticleBySlug(slug)
        CommentDao.new {
            body = postComment.comment.body
            author = UserDao.find { (UserTable.email) eq loggedInUser.email }.first()
            this.article = article
        }.toComment()
    }

    suspend fun getAllComments(loggedInUser: User?, slug: String): List<Comment> = newSuspendedTransaction {
        val article = getArticleBySlug(slug)
        CommentDao.find {
            (CommentTable.article eq article.id)
        }.map { commentDao ->
            if (loggedInUser != null) {
                val currentUser = UserDao.find { (UserTable.email) eq loggedInUser.email }.first()
                val isFollowing = checkIfUserFollowsAuthor(currentUser, commentDao.author)
                commentDao.toComment(isFollowingAuthor = isFollowing)
            }
            commentDao.toComment(isFollowingAuthor = false)
        }
    }

    suspend fun deleteComment(loggedInUser: User, slug: String, commentId: String) = newSuspendedTransaction {
        val article = getArticleBySlug(slug)
        val author = UserDao.find { (UserTable.email) eq loggedInUser.email }.first()
        val comment = CommentDao.find {
            ((CommentTable.article eq article.id) and (CommentTable.id eq commentId.toUUID()))
        }.firstOrNull() ?: throw CommentDoesNotExistException("Comment with id: $commentId does not exist")
        if (comment.author != author) throw AuthorizationException("You are not authorized to delete this comment!")
        comment.delete()
    }

}