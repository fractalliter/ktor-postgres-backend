package com.personia.models

import org.jetbrains.exposed.sql.Table

object Nodes : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 128).uniqueIndex()
    val supervisor = varchar("supervisor", 128)
    override val primaryKey = PrimaryKey(id)
}