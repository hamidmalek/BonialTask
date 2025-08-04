package com.example.bonialtask.model

data class BannerContent(
    val id: String,
    val publisher: Publisher,
    val publishedFrom: String,
    val publishedUntil: String,
    val clickUrl: String,
    val imageUrl: String
)
