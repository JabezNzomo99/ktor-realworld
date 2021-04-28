package com.example.articles

import com.example.core.db.ExtendedUUIDEntity
import com.example.core.db.ExtendedUUIDEntityClass
import com.example.core.db.ExtendedUUIDTable
import com.example.users.User
import com.example.users.UserDao
import com.example.users.UserTable
import com.example.users.toUser
import com.google.gson.annotations.SerializedName
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

data class PostArticle(val article: CreateArticle)

data class CreateArticle(
    val title: String,
    val description: String,
    val body: String,
    @SerializedName("tagList")
    val tags: List<String>?,
)

data class PutArticle(val article: UpdateArticle)

data class UpdateArticle(
    val title: String?,
    val description: String?,
    val body: String?,
    @SerializedName("tagList")
    val tags: List<String>?,
)

data class Article(
    val slug: String,
    val title: String,
    val description: String,
    val body: String,
    @SerializedName("tagList")
    val tags: List<String>?,
    val createdAt: String,
    val updatedAt: String?,
    val favorited: Boolean,
    val favoritesCount: Int,
    val author: User,
)

data class PostComment(val comment: AddComment)

data class AddComment(val body: String)

data class Comment(
    val id: UUID,
    val body: String,
    val createdAt: String,
    val updatedAt: String?,
    val author: User,
)

object ArticleTable : ExtendedUUIDTable(name = "articles", pk = "article_id") {
    val title = varchar(name = "title", length = 255)
    val slug = varchar(name = "slug", length = 255)
    val authorId = reference("author_id", UserTable)
    val description = varchar(name = "description", length = 255)
    val body = text(name = "body")
    val tags = varchar(name = "tags", length = 255).nullable()
}

class ArticleDao(id: EntityID<UUID>) : ExtendedUUIDEntity(id, ArticleTable) {
    companion object : ExtendedUUIDEntityClass<ArticleDao>(ArticleTable)

    var title by ArticleTable.title
    var author by UserDao referencedOn ArticleTable.authorId
    var slug by ArticleTable.slug
    var description by ArticleTable.description
    var body by ArticleTable.body
    var tags by ArticleTable.tags
}

object UserFavoriteTable : ExtendedUUIDTable(name = "user_favourites") {
    val userId = reference("user_id", UserTable)
    val articleId = reference("article_id", ArticleTable)
    override val primaryKey: PrimaryKey = PrimaryKey(userId, articleId)
}

class UserFavoriteDao(id: EntityID<UUID>) : ExtendedUUIDEntity(id, UserFavoriteTable) {
    companion object : ExtendedUUIDEntityClass<UserFavoriteDao>(UserFavoriteTable)

    var user by UserDao referencedOn UserFavoriteTable.userId
    var article by ArticleDao referencedOn UserFavoriteTable.articleId
}

object CommentTable : ExtendedUUIDTable(name = "comments") {
    val body = text(name = "body")
    val article = reference("article_id", ArticleTable)
    val author = reference("author_id", UserTable)
}

class CommentDao(id: EntityID<UUID>) : ExtendedUUIDEntity(id, CommentTable) {
    companion object : ExtendedUUIDEntityClass<CommentDao>(CommentTable)

    var body by CommentTable.body
    var article by ArticleDao referencedOn CommentTable.article
    var author by UserDao referencedOn CommentTable.author
}

fun ArticleDao.toArticle(isFavorite: Boolean = false, favoritesCount: Int = 0, isFollowing: Boolean? = null): Article {
    return Article(
        slug = this.slug,
        title = this.title,
        description = this.description,
        body = this.body,
        tags = this.tags?.split(",")?.toList(),
        createdAt = this.createdAt.toString(),
        updatedAt = this.updatedAt?.toString(),
        favorited = isFavorite,
        favoritesCount = favoritesCount,
        author = User(
            username = this.author.username,
            bio = this.author.bio,
            image = this.author.image,
            email = this.author.email,
            isFollowing = isFollowing,
        ),
    )
}

fun CommentDao.toComment(isFollowingAuthor: Boolean? = null): Comment {
    return Comment(id = this.uid,
        body = this.body,
        author = this.author.toUser(isFollowing = isFollowingAuthor),
        createdAt = this.createdAt.toString(),
        updatedAt = this.updatedAt?.toString())
}
