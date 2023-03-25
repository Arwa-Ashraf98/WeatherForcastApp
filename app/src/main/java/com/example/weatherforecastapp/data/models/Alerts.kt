package com.example.weatherforecastapp.data.models

data class Alerts(
    val description: String,
    val end: Long,
    val event: String,
    val sender_name: String,
    val start: Long,
    val tags: List<String>
)