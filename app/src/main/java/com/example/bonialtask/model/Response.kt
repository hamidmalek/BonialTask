package com.example.bonialtask.model

import com.squareup.moshi.Json

data class Response(
    @Json(name = "_embedded") val embedded: Embedded
)
