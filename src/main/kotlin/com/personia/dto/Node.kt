package com.personia.dto

import kotlinx.serialization.Serializable

@Serializable
data class Node(val id: Int, val name: String, val supervisor: String)