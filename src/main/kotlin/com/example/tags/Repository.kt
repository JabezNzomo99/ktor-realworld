package com.example.tags

import com.example.core.db.now
import com.example.core.db.upsert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class Repository {
    suspend fun getAll(): List<String> = newSuspendedTransaction {
        TagDao.all().map { tag ->
            tag.name
        }
    }

    suspend fun add(tags: List<String>) = newSuspendedTransaction {
        tags.forEach { tag ->
            TagTable.upsert(TagTable.name) {
                it[name] = tag
                it[createdAt] = now()
            }
        }
    }
}