package com.example.core

import org.mindrot.jbcrypt.BCrypt
import java.util.*

fun String?.encrypt(): String = BCrypt.hashpw(this, BCrypt.gensalt())

fun String?.compare(hash: String): Boolean = BCrypt.checkpw(this, hash)

fun String?.toUUID(): UUID = try {
    UUID.fromString(this)
} catch (exception: Exception) {
    throw InvalidUUIDException(exception.message)
}



