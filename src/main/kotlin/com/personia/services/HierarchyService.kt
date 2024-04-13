package com.personia.services

import com.personia.dao.node.NodeDaoFacade
import com.personia.dao.node.nodeDAO
import com.personia.dto.Node
import com.personia.utils.Graph
import io.ktor.server.plugins.*

class NodeService(private val nodeRepository: NodeDaoFacade) {
    suspend fun getHierarchy(): HashMap<String, Map<String, Any>> {
        val graph = Graph()
        val hierarchy = nodeRepository.allConnections()
        hierarchy.forEach { (_, node, supervisor) -> graph.connect(supervisor, node) }
        val root = graph.findRoot().root
        val dfs = root?.let { graph.dfs(it) }!!
        val tempMap = graph.transformToNestedMap(dfs)
        val rootValue = tempMap[root] ?: mapOf()
        return hashMapOf(root to rootValue)
    }

    suspend fun findByName(name: String): Node? = nodeRepository.findByName(name)

    @kotlin.jvm.Throws(java.lang.AssertionError::class)
    suspend fun createHierarchy(hierarchy: Map<String, String>): HashMap<String, Map<String, Any>> {
        val graph = Graph()
        hierarchy.forEach { (node, supervisor) -> graph.connect(supervisor, node) }
        val root = graph.findRoot().root
        val dfs = root?.let { graph.dfs(it) }!!
        hierarchy.forEach { (node, supervisor) -> nodeRepository.upsertNode(node, supervisor) }
        val tempMap = graph.transformToNestedMap(dfs)
        val rootValue = tempMap[root] ?: mapOf()
        return hashMapOf(root to rootValue)
    }

    suspend fun retrieveSupervisors(name: String, level: Int): HashMap<String, Map<String, Any>?> {
        findByName(name) ?: throw NotFoundException("User not found")
        val graph = Graph()
        nodeRepository.allConnections().forEach { graph.connect(it.name, it.supervisor) }
        val visited = graph.dfs(name, level)
        val tmpMap = graph.transformToNestedMap(visited)
        return hashMapOf(name to tmpMap[name])
    }

}

val nodeService = NodeService(nodeDAO)