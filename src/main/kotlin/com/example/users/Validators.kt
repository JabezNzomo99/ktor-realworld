package com.example.users

import org.valiktor.ConstraintViolationException
import org.valiktor.functions.isNotEmpty
import org.valiktor.functions.isNotNull
import org.valiktor.functions.validate

import kotlin.jvm.Throws

@Throws(ConstraintViolationException::class)
fun PostUser.validate() {
    org.valiktor.validate(this) {
        validate(PostUser::user).isNotNull()

        validate(PostUser::user).validate {
            validate(User::email).apply {
                isNotNull()
                isNotEmpty()
            }

            validate(User::password).apply {
                isNotNull()
                isNotEmpty()
            }

            validate(User::username).apply {
                isNotNull()
                isNotEmpty()
            }
        }
    }
}

@Throws(ConstraintViolationException::class)
fun PostLogin.validate() {
    org.valiktor.validate(this) {
        validate(PostLogin::user).isNotNull()
        validate(PostLogin::user).validate {
            validate(LoginUser::email).apply {
                isNotNull()
                isNotEmpty()
            }

            validate(LoginUser::password).apply {
                isNotNull()
                isNotEmpty()
            }
        }
    }
}

@Throws(ConstraintViolationException::class)
fun PutUser.validate() {
    org.valiktor.validate(this) {
        validate(PutUser::user).isNotNull()
    }
}
