package com.personia.dao.node

import com.personia.models.Node

interface NodeDaoFacade {
    suspend fun allConnections(): List<Node>
    suspend fun findByName(name: String): Node?
    suspend fun updateSupervisor(name:String, supervisor: String): Boolean
    suspend fun addNewNode(name: String, supervisor: String): Node?
    suspend fun upsertNode(name: String, supervisor: String): Node?
}