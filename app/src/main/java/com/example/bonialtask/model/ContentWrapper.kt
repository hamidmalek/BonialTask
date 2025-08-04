package com.example.bonialtask.model

import com.squareup.moshi.JsonClass

sealed class ContentWrapper {
    abstract val placement: String
    abstract val adFormat: String?
    abstract val contentFormatSource: String
    abstract val contentType: String
    abstract val externalTracking: ExternalTracking

    @JsonClass(generateAdapter = true)
    data class SuperBannerCarousel(
        override val placement: String,
        override val adFormat: String?,
        override val contentFormatSource: String,
        override val contentType: String,
        val content: List<BannerSlot>,
        override val externalTracking: ExternalTracking
    ) : ContentWrapper()

    @JsonClass(generateAdapter = true)
    data class Brochure(
        override val placement: String,
        override val adFormat: String?,
        override val contentFormatSource: String,
        override val contentType: String,
        val content: BrochureContent,
        override val externalTracking: ExternalTracking
    ) : ContentWrapper()
}

