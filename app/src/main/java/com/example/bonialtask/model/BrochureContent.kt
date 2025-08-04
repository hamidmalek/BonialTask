package com.example.bonialtask.model

data class BrochureContent(
    val id: Long,
    val contentId: String,
    val title: String,
    val validFrom: String,
    val validUntil: String,
    val publishedFrom: String,
    val publishedUntil: String,
    val type: String,
    val brochureImage: String,
    val pageCount: Int,
    val publisher: Publisher,
    val contentBadges: List<Badge>,
    val distance: Double,
    val hideValidityDate: Boolean,
    val closestStore: Store
)
