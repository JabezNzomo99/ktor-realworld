package com.example

import com.example.articles.articleModules
import com.example.core.coreModules
import com.example.core.db.initORM
import com.example.tags.tagModules
import com.example.users.userModules
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.junit.jupiter.api.BeforeEach
import org.koin.core.context.startKoin
import org.koin.test.junit5.AutoCloseKoinTest
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
open class BaseApiTest: AutoCloseKoinTest() {

    companion object {
        @Container
        private val dbContainer: PostgreSQLContainer<*> = PostgreSQLContainer<Nothing>("postgres:13-alpine")
    }

    @BeforeEach
    fun setUp() {
        initKoin()
        initDB()
    }

    private fun initKoin(){
        startKoin {
            modules(coreModules)
            modules(userModules)
            modules(articleModules)
            modules(tagModules)
        }
    }

    private fun initDB() {
        dbContainer.start()
        val config = HikariConfig().apply {
            jdbcUrl = dbContainer.jdbcUrl
            username = dbContainer.username
            password = dbContainer.password
            maximumPoolSize = 4
            validate()
        }
        initORM(HikariDataSource(config))
    }
}

