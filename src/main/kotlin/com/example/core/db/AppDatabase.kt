package com.example.core.db

import com.example.articles.ArticleTable
import com.example.articles.Comment
import com.example.articles.CommentTable
import com.example.articles.UserFavoriteTable
import com.example.core.AppConfig
import com.example.tags.TagTable
import com.example.users.UserFollowingTable
import com.example.users.UserTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import javax.sql.DataSource

object AppDatabase {

    private lateinit var dataSource: DataSource

    fun init() {
        initDataSource()
        migrate()
        initORM()
    }

    private fun initDataSource() {
        val config = HikariConfig()
        config.jdbcUrl = AppConfig.DB.jdbc
        config.username = AppConfig.DB.user
        config.password = AppConfig.DB.password
        config.isAutoCommit = false
        config.maximumPoolSize = 3
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        config.validate()
        dataSource = HikariDataSource(config)
    }

    private fun migrate() {
        val table = "classpath:db/migration/table"
        val include = AppConfig.DB.migrationList.map { path -> "classpath:$path" }
        val locations = arrayOf(table) + include

        val flyway = Flyway.configure()
            .dataSource(dataSource)
            .locations(*locations)
            .load()
        flyway.migrate()
    }

    private fun initORM() {
        Database.connect(dataSource)
        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                UserTable,
                ArticleTable,
                TagTable,
                UserFavoriteTable,
                CommentTable,
                UserFollowingTable,
            )
        }
    }
}