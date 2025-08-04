package com.example.bonialtask.model

data class Store(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val street: String,
    val streetNumber: String,
    val zip: String,
    val city: String
)
