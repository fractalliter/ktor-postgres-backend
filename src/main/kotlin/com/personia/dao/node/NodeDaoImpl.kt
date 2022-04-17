package com.personia.dao.node

import com.personia.dao.DatabaseFactory.dbQuery
import com.personia.models.Node
import com.personia.models.Nodes
import org.jetbrains.exposed.sql.*

class NodeDaoFacadeImp: NodeDaoFacade {

    private fun resultRowToNode(row: ResultRow) = Node(
        id = row[Nodes.id],
        name = row[Nodes.name],
        supervisor = row[Nodes.supervisor],
    )
    override suspend fun allConnections(): List<Node> = dbQuery{
        Nodes.selectAll().map(::resultRowToNode)
    }

    override suspend fun addNewNode(name: String, supervisor: String): Node? = dbQuery {
        val insertStatement = Nodes.insert {
            it[Nodes.name] = name
            it[Nodes.supervisor] = supervisor
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToNode)
    }

    override suspend fun findByName(name: String): Node? = dbQuery {
        Nodes
            .select { Nodes.name eq name }
            .map(::resultRowToNode)
            .singleOrNull()
    }

    override suspend fun updateSupervisor(name: String, supervisor: String): Boolean = dbQuery {
        Nodes.update( { Nodes.name eq name }){
            it[Nodes.supervisor] = supervisor
        } > 0
    }

    override suspend fun upsertNode(name: String, supervisor: String): Node? = dbQuery {
        val node: Node? = Nodes.select { Nodes.name eq name }.map(::resultRowToNode)
            .singleOrNull()
        if(node != null){
            updateSupervisor(name, supervisor)
            findByName(name)
        }else addNewNode(name, supervisor)
    }
}

val nodeDAO: NodeDaoFacade = NodeDaoFacadeImp()