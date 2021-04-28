package com.example.users

data class AuthenticationException(override val message: String?) : RuntimeException(message)

data class AuthorizationException(override val message: String?) : RuntimeException(message)

data class UserNotFoundException(override val message: String?) : RuntimeException(message)