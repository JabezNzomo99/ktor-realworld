package com.example.users

import com.example.core.JwtConfig
import com.example.core.compare
import com.example.core.encrypt
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.*

class Repository {
    suspend fun create(postUser: PostUser): UserResponse = newSuspendedTransaction {
        val user = UserDao.new {
            email = postUser.user.email
            username = postUser.user.username
            password = postUser.user.password.encrypt()
            bio = postUser.user.bio
            image = postUser.user.image
        }
        user.toUserResponse(token = JwtConfig.makeToken(user))
    }

    suspend fun update(loggedInUser: User, putUser: PutUser): UserResponse = newSuspendedTransaction {
        UserDao.find {
            (UserTable.email eq loggedInUser.email)
        }.first().apply {
            if (!putUser.user.email.isNullOrEmpty()) this.email = putUser.user.email
            if (!putUser.user.username.isNullOrEmpty()) username = putUser.user.username
            if (!putUser.user.password.isNullOrEmpty()) password = putUser.user.password.encrypt()
            bio = putUser.user.bio
            image = putUser.user.image
        }.toUserResponse()
    }

    suspend fun login(postLogin: PostLogin): UserResponse = newSuspendedTransaction {
        val user = UserDao.find {
            (UserTable.email eq postLogin.user.email)
        }.firstOrNull() ?: throw AuthenticationException("Invalid email or Password")
        if (postLogin.user.password.compare(user.password)) {
            val token = JwtConfig.makeToken(user)
            return@newSuspendedTransaction user.toUserResponse(token)
        } else {
            throw AuthenticationException("Invalid email or Password")
        }
    }

    suspend fun findUserById(id: UUID) = newSuspendedTransaction {
        UserDao.find {
            (UserTable.id eq id)
        }.firstOrNull()
    }

    suspend fun findUserByUsername(loggedInUser: User?, username: String): User = newSuspendedTransaction {
        val user = UserDao.find {
            UserTable.username eq username
        }.firstOrNull() ?: throw UserNotFoundException("User with username:$username is not found")
        if (loggedInUser != null) {
            val currentUser = UserCache.get(loggedInUser.id)
            val isFollowing = UserFollowingDao.find {
                ((UserFollowingTable.userId eq currentUser?.uid) and (UserFollowingTable.followingId eq user.id))
            }.empty()
            return@newSuspendedTransaction user.toUser(isFollowing = !isFollowing)
        }
        user.toUser()
    }

    suspend fun followUser(loggedInUser: User, username: String): User = newSuspendedTransaction {
        val currentUser = UserCache.get(loggedInUser.id)!!
        val following = UserDao.find {
            UserTable.username eq username
        }.firstOrNull() ?: throw UserNotFoundException("User with username:$username is not found")
        UserFollowingDao.new {
            this.userId = currentUser
            this.followingId = following
        }
        following.toUser(isFollowing = true)
    }

    suspend fun unfollowUser(loggedInUser: User, username: String): User = newSuspendedTransaction {
        val currentUser = UserCache.get(loggedInUser.id)
        val following = UserDao.find {
            UserTable.username eq username
        }.firstOrNull() ?: throw UserNotFoundException("User with username:$username is not found")
        UserFollowingDao.find {
            ((UserFollowingTable.userId eq currentUser?.uid) and (UserFollowingTable.followingId eq following.id))
        }.firstOrNull()?.delete()
        following.toUser(isFollowing = false)
    }
}