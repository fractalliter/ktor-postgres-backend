package com.personia.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class Node(val id: Int, val name: String, val supervisor: String)

object Nodes : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 128).uniqueIndex()
    val supervisor = varchar("supervisor", 128)
    override val primaryKey = PrimaryKey(id)
}