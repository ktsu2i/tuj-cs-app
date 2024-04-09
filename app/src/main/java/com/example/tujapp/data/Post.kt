package com.example.tujapp.data

import com.example.tujapp.R

data class Post (
    val postId: String? = null,
    val userId: String = "",
    val title: String = "",
    val content: String = "",
    val createdAt: Long = System.currentTimeMillis(),
)