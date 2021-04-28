package com.example.users

import com.example.core.db.ExtendedUUIDEntity
import com.example.core.db.ExtendedUUIDEntityClass
import com.example.core.db.ExtendedUUIDTable
import io.ktor.auth.*
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

data class PostUser(val user: User)

data class PostLogin(val user: LoginUser)

data class PutUser(val user: UpdateUser)

data class UserResponse(val user: User)

data class LoginUser(
    val email: String,
    val password: String,
)

data class UpdateUser(
    val email: String?,
    val password: String?,
    val username: String?,
    val bio: String?,
    val image: String?,
)

data class User(
    val id: UUID? = null,
    val email: String,
    val password: String? = null,
    val username: String,
    val bio: String?,
    val image: String?,
    val token: String? = null,
    val isFollowing: Boolean? = null,
) : Principal

object UserTable : ExtendedUUIDTable(name = "users", pk = "user_id") {
    val email = varchar(name = "email", length = 255).uniqueIndex()
    val username = varchar(name = "username", length = 255).uniqueIndex()
    val password = varchar(name = "password", length = 255)
    val bio = text(name = "bio").nullable()
    val image = varchar("image", length = 255).nullable()
}

class UserDao(id: EntityID<UUID>) : ExtendedUUIDEntity(id, UserTable) {
    companion object : ExtendedUUIDEntityClass<UserDao>(UserTable)

    var email by UserTable.email
    var username by UserTable.username
    var password by UserTable.password
    var bio by UserTable.bio
    var image by UserTable.image
}

object UserFollowingTable : ExtendedUUIDTable("user_followings") {
    val userId = reference("user_id", UserTable)
    val followingId = reference("following_id", UserTable)
    override val primaryKey: PrimaryKey = PrimaryKey(userId, followingId)
}

class UserFollowingDao(id: EntityID<UUID>) : ExtendedUUIDEntity(id, UserTable) {
    companion object : ExtendedUUIDEntityClass<UserFollowingDao>(UserFollowingTable)

    var userId by UserDao referencedOn UserFollowingTable.userId
    var followingId by UserDao referencedOn UserFollowingTable.followingId
}

fun UserDao.toUserResponse(token: String? = null): UserResponse {
    return UserResponse(this.toUser(token))
}

fun UserDao.toUser(token: String? = null, isFollowing: Boolean? = null): User {
    return User(
        id = this.uid,
        email = this.email,
        password = null,
        username = this.username,
        bio = this.bio,
        image = this.image,
        token = token,
        isFollowing = isFollowing,
    )
}