package com.example.core.db

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Index
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.TransactionManager

class UpsertStatement<Key : Any>(table: Table, conflictColumn: Column<*>? = null, conflictIndex: Index? = null) :
        InsertStatement<Key>(table, false) {

    private val indexName: String
    private val indexColumns: List<Column<*>>
    private val index: Boolean

    init {
        when {
            conflictIndex != null -> {
                index = true
                indexName = conflictIndex.indexName
                indexColumns = conflictIndex.columns
            }
            conflictColumn != null -> {
                index = false
                indexName = conflictColumn.name
                indexColumns = listOf(conflictColumn)
            }
            else -> throw IllegalArgumentException()
        }
    }

    override fun prepareSQL(transaction: Transaction) = buildString {
        append(super.prepareSQL(transaction))

        val dialect = transaction.db.vendor
        if (dialect == "postgresql") {
            if (index) {
                append(" ON CONFLICT ON CONSTRAINT ")
                append(indexName)
            } else {
                append(" ON CONFLICT(")
                append(indexName)
                append(")")
            }
            append(" DO UPDATE SET ")

            values.keys.filter { it !in indexColumns }
                    .joinTo(this) { "${transaction.identity(it)}=EXCLUDED.${transaction.identity(it)}" }

        } else {

            append(" ON DUPLICATE KEY UPDATE ")
            values.keys.filter { it !in indexColumns }
                    .joinTo(this) { "${transaction.identity(it)}=VALUES(${transaction.identity(it)})" }

        }
    }

}

inline fun <T : Table> T.upsert(
        conflictColumn: Column<*>? = null,
        conflictIndex: Index? = null,
        body: T.(UpsertStatement<Number>) -> Unit,
) =
        UpsertStatement<Number>(this, conflictColumn, conflictIndex).apply {
            body(this)
            execute(TransactionManager.current())
        }

