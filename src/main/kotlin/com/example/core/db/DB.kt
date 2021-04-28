package com.example.core.db

import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.`java-time`.datetime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

fun now() = ZonedDateTime.now(ZoneId.of("UTC")).toLocalDateTime()

abstract class ExtendedUUIDTable(name: String, pk: String = "uid") :
        UUIDTable(name = name, columnName = pk) {
    val createdAt = datetime("create_at").clientDefault { now() }
    val updatedAt = datetime("update_at").nullable()
    val deletedAt = datetime("delete_at").nullable()
}

abstract class ExtendedUUIDEntity(id: EntityID<UUID>, table: ExtendedUUIDTable) : UUIDEntity(id) {
    val createdAt by table.createdAt
    var updatedAt by table.updatedAt
    private var deletedAt by table.deletedAt

    val uid: UUID
        get() = id.value

    override fun delete() {
        deletedAt = now()
    }
}

abstract class ExtendedUUIDEntityClass<Entity : ExtendedUUIDEntity>(private val extendedTable: ExtendedUUIDTable) :
        UUIDEntityClass<Entity>(extendedTable) {

    private var includeSoftDeleted = false

    init {
        EntityHook.subscribe { action ->
            if (action.changeType == EntityChangeType.Updated) {
                try {
                    action.toEntity(this)?.updatedAt = now()
                } catch (ignore: Exception) {
                }
            }
        }
    }

    fun includeDeleted() = apply {
        includeSoftDeleted = true
    }

    override fun all(): SizedIterable<Entity> {
        return if (includeSoftDeleted) {
            all()
        } else {
            wrapRows(table.select { extendedTable.deletedAt.isNull() }.notForUpdate())
        }
    }

    override fun searchQuery(op: Op<Boolean>): Query {
        var query = super.searchQuery(op)
        if (!includeSoftDeleted) {
            query = query.andWhere { extendedTable.deletedAt.isNull() }
        }
        return query
    }
}
