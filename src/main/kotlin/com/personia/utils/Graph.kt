package com.personia.utils

import java.util.ArrayDeque
import java.util.stream.Collectors


class Graph {
    var root: String? = null
    val adjacent: HashMap<String, HashSet<String>> = LinkedHashMap()

    fun connect(source: String, dest: String) {
        val sourceNeighbours = adjacent.getOrDefault(source, HashSet())
        sourceNeighbours.add(dest)
        adjacent[source] = sourceNeighbours
    }

    @Throws(Exception::class)
    fun findRoot(): Graph {
        val roots = adjacent.keys.stream()
            .filter { k: String ->
                !adjacent.values.stream().flatMap { obj: HashSet<String> -> obj.stream() }
                    .collect(Collectors.toSet())
                    .contains(k)
            }
            .collect(Collectors.toList())
        if (roots.size == 0) {
            throw Exception("There is no root")
        }
        if (roots.size > 1) {
            throw Exception("Too many roots [$roots]")
        }
        this.root = roots[0]
        return this
    }

    private fun convertToHashMap(): HashMap<String, HashMap<String, Any>> {
        val tempMap = HashMap<String, HashMap<String, Any>>()
        for (k in adjacent.keys){
            val t = HashMap<String, Any>()
            adjacent[k]?.forEach {
                t[it] = emptyMap<String, Any>()
            }
            tempMap[k] = t
        }
        return tempMap
    }

    fun transformToNested(dfsVisited: HashSet<String>): HashMap<String, HashMap<String, Any>> {
        val tempMap = convertToHashMap()
        val visited = ArrayDeque(dfsVisited)
        for (i in 1..visited.size){
            val node = visited.pop()
            val supervisor = tempMap.filterValues { it.containsKey(node) }.keys.toList()
            if (supervisor.isNotEmpty()) {
                val supValues = tempMap.getOrDefault(supervisor[0], HashMap())
                supValues[node] = tempMap.getOrDefault(node, HashMap())
                tempMap[supervisor[0]] = supValues
            }
        }
        return tempMap
    }

    @Throws(Exception::class)
    fun dfs(start: String): HashSet<String> {
        val visited = HashSet<String>()
        dfsRecursive(start, visited)
        return visited
    }

    @Throws(Exception::class)
    fun dfs(start: String, depth: Int): HashSet<String> {
        val visited = HashSet<String>()
        dfsRecursive(start, visited, depth)
        return visited
    }

    @Throws(Exception::class)
    private fun dfsRecursive(current: String, visited: HashSet<String>) {
        visited.add(current)
        for (sub in adjacent.getOrDefault(current, HashSet())) {
            if (visited.add(sub)) {
                dfsRecursive(sub, visited)
            } else {
                val culprit = adjacent[sub]!!
                    .stream().filter { i: String ->
                        adjacent[i]!!
                            .contains(sub)
                    }
                    .collect(Collectors.toList())[0]
                throw Exception("there is a loop in the hierarchy.$sub is already supervisor of $culprit")
            }
        }
    }

    @Throws(Exception::class)
    private fun dfsRecursive(current: String, visited: HashSet<String>, depth: Int) {
        visited.add(current)
        for (sub in adjacent.getOrDefault(current, HashSet())) {
            if (visited.add(sub) && visited.size < depth) {
                dfsRecursive(sub, visited, depth)
            }
        }
    }
}