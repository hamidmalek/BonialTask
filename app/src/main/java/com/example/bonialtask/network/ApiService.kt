package com.example.bonialtask.network

import com.example.bonialtask.model.Response
import retrofit2.http.GET

interface ApiService {
    @GET("shelf.json")
    suspend fun getShelf(): Response

    companion object {
        const val BASE_URL = "https://mobile-s3-test-assets.aws-sdlc-bonial.com/"
    }
}