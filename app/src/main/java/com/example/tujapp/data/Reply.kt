package com.example.tujapp.data

data class Reply (
    val userId: String = "",
    val content: String = "",
    val createdAt: Long = System.currentTimeMillis(),
)