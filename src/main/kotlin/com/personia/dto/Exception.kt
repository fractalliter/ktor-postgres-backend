package com.personia.dto

import kotlinx.serialization.Serializable

@Serializable
data class Exception(val message: String?, val code: Int)