package com.personia.utils

import java.util.*


/**
 * Create a directional Graph and travers over it with help of Depth First Search algorithm
 */
class Graph {
    var root: String? = null
    private val adjacent: MutableMap<String, MutableSet<String>> = LinkedHashMap()

    /**
     * Creates a directional graph
     */
    fun connect(source: String, dest: String) {
        val sourceNeighbours = adjacent[source] ?: mutableSetOf()
        sourceNeighbours.add(dest)
        adjacent[source] = sourceNeighbours
    }

    /**
     * First checks if the Graph is a Tree
     * Second checks if it's unified Tree
     * At the end sets the root to the Tree and return the Tree
     */
    @Throws(AssertionError::class)
    fun findRoot(): Graph {
        val values = adjacent.values.flatten().toSet()
        val roots = adjacent.keys.filter { !values.contains(it) }
        if (roots.isEmpty()) {
            throw AssertionError("There is no root. there might be loops in the hierarchy.")
        }
        if (roots.size > 1) {
            throw AssertionError("Too many roots [$roots]")
        }
        this.root = roots.first()
        return this
    }

    /**
     * Converts the adjacent nodes of a node form Set to Map
     */
    private fun convertToHashMap(): MutableMap<String, MutableMap<String, Any>> {
        val tempMap = mutableMapOf<String, MutableMap<String, Any>>()
        for (k in adjacent.keys) {
            val t = mutableMapOf<String, Any>()
            adjacent[k]?.forEach { t[it] = emptyMap<String, Any>() }
            tempMap[k] = t
        }
        return tempMap
    }

    /**
     * Transforms the Graph adjacent from Map to a Nested Map
     */
    fun transformToNestedMap(dfsVisited: Set<String>): Map<String, Map<String, Any>> {
        val tempMap = convertToHashMap()
        val visited = ArrayDeque(dfsVisited)
        for (i in 1..visited.size) {
            val node = visited.pop()
            val supervisor = tempMap.filterValues { it.containsKey(node) }.keys.toList()
            if (supervisor.isNotEmpty()) {
                val s1 = supervisor.first()
                val supValues = tempMap[s1] ?: mutableMapOf()
                supValues[node] = tempMap[node] ?: mutableMapOf<String, MutableMap<String, Any>>()
                tempMap[s1] = supValues
            }
        }
        return tempMap
    }

    /**
     * Traverse the Graph from a starting node
     * @exception AssertionError If Graph contains a cycle will throw an assertion error
     */
    @Throws(AssertionError::class)
    fun dfs(start: String): MutableSet<String> {
        val visited = mutableSetOf<String>()
        dfsRecursive(start, visited)
        return visited
    }

    /**
     * Traverse the Graph from a starting node to a specified depth
     */
    fun dfs(start: String, depth: Int): Set<String> {
        val visited = mutableSetOf<String>()
        dfsRecursive(start, visited, depth)
        return visited
    }

    /**
     * Recursively traverse the Graph from a starting point
     * @exception AssertionError If Graph contains a cycle will throw an assertion error
     */
    @Throws(AssertionError::class)
    private fun dfsRecursive(current: String, visited: MutableSet<String>) {
        visited.add(current)
        for (sub in adjacent[current] ?: setOf()) {
            if (visited.add(sub)) {
                dfsRecursive(sub, visited)
            } else {
                val culprit = adjacent[sub]?.first { adjacent[it]?.contains(sub) ?: false }
                throw AssertionError("there is a loop in the hierarchy.$sub is already supervisor of $culprit")
            }
        }
    }

    /**
     * Recursively traverse the Graph from a starting point to a specified depth
     */
    private fun dfsRecursive(current: String, visited: MutableSet<String>, depth: Int) {
        visited.add(current)
        for (sub in adjacent[current] ?: setOf()) {
            if (visited.add(sub) && visited.size < depth) {
                dfsRecursive(sub, visited, depth)
            }
        }
    }
}