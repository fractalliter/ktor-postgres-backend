package com.personia.services

import com.personia.dao.node.nodeDAO
import com.personia.models.Node
import com.personia.utils.Graph

class NodeService {

    suspend fun findAll(): List<Node> = nodeDAO.allConnections()

    suspend fun findByName(name: String): Node? = nodeDAO.findByName(name)

    suspend fun createHierarchy(hierarchy: Map<String, String>): Map<String?, HashMap<String, Any>?> {
        val graph = Graph()
        hierarchy.forEach { (node, supervisor) -> graph.connect(supervisor, node) }
        val root = graph.findRoot().root
        var dfs: HashSet<String> = HashSet()
        if (root != null) {
            dfs = graph.dfs(root)
        }
        hierarchy.forEach { (node, supervisor) -> nodeDAO.upsertNode(node, supervisor) }
        val tempMap = graph.transformToNested(dfs)
        return mapOf(root to tempMap[root])
    }

    suspend fun retrieveSupervisors(name: String, level: Int): Map<String, HashMap<String, Any>?> {
        val graph = Graph()
        nodeDAO.allConnections().forEach { graph.connect(it.name,it.supervisor) }
        val visited = graph.dfs(name, level)
        val tmpMap = graph.transformToNested(visited)
        return mapOf(name to tmpMap[name])
    }

}

val nodeService = NodeService()