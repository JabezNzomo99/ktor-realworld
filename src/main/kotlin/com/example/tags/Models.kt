package com.example.tags

import com.example.core.db.ExtendedUUIDEntity
import com.example.core.db.ExtendedUUIDEntityClass
import com.example.core.db.ExtendedUUIDTable
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

object TagTable : ExtendedUUIDTable(name = "tags", pk = "tag_id") {
    val name = varchar(name = "tag", length = 50).uniqueIndex()
}

class TagDao(id: EntityID<UUID>) : ExtendedUUIDEntity(id, TagTable) {
    companion object : ExtendedUUIDEntityClass<TagDao>(TagTable)

    var name by TagTable.name
}
