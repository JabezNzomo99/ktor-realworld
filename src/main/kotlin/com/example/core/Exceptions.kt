package com.example.core

data class MissingRequestBodyException(override val message: String?) : RuntimeException(message)
data class InvalidUUIDException(override val message: String?) : RuntimeException(message)