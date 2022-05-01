package com.personia.services

import com.personia.dao.node.nodeDAO
import com.personia.models.Node
import com.personia.utils.Graph
import io.ktor.server.plugins.*

class NodeService {
    suspend fun getHierarchy(): Map<String, Map<String, Any>> {
        val graph = Graph()
        val hierarchy = nodeDAO.allConnections()
        hierarchy.forEach { (_, node, supervisor) -> graph.connect(supervisor, node) }
        val root = graph.findRoot().root
        val dfs = root?.let { graph.dfs(it) }!!
        val tempMap = graph.transformToNestedMap(dfs)
        val rootValue = tempMap[root] ?: mapOf()
        return mapOf(root to rootValue)
    }

    suspend fun findByName(name: String): Node? = nodeDAO.findByName(name)

    @kotlin.jvm.Throws(java.lang.AssertionError::class)
    suspend fun createHierarchy(hierarchy: Map<String, String>): Map<String?, Map<String, Any>> {
        val graph = Graph()
        hierarchy.forEach { (node, supervisor) -> graph.connect(supervisor, node) }
        val root = graph.findRoot().root
        val dfs = root?.let { graph.dfs(it) }!!
        hierarchy.forEach { (node, supervisor) -> nodeDAO.upsertNode(node, supervisor) }
        val tempMap = graph.transformToNestedMap(dfs)
        val rootValue = tempMap[root] ?: mapOf()
        return mapOf(root to rootValue)
    }

    suspend fun retrieveSupervisors(name: String, level: Int): Map<String, Map<String, Any>?> {
        findByName(name) ?: throw NotFoundException("User not found")
        val graph = Graph()
        nodeDAO.allConnections().forEach { graph.connect(it.name,it.supervisor) }
        val visited = graph.dfs(name, level)
        val tmpMap = graph.transformToNestedMap(visited)
        return mapOf(name to tmpMap[name])
    }

}

val nodeService = NodeService()