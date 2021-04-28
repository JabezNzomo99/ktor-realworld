package com.example.users

class Controller(private val repository: Repository) {

    suspend fun register(postUser: PostUser): UserResponse {
        postUser.validate()
        return repository.create(postUser)
    }

    suspend fun login(postLogin: PostLogin): UserResponse {
        postLogin.validate()
        return repository.login(postLogin)
    }

    suspend fun put(loggedInUser: User, putUser: PutUser): UserResponse {
        putUser.validate()
        return repository.update(loggedInUser, putUser)
    }

    suspend fun getProfile(loggedInUser: User?, username: String): User {
        return repository.findUserByUsername(loggedInUser, username)
    }

    suspend fun followUser(loggedInUser: User, username: String): User {
        return repository.followUser(loggedInUser, username)
    }

    suspend fun unfollowUser(loggedInUser: User, username: String): User {
        return repository.unfollowUser(loggedInUser, username)
    }
}