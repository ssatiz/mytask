package com.influx.marcus.theatres.api.ApiModels.theatreshowtime


data class TheatreShowtimeRequest(
    val theatre_code: String,
    val app_version: String,
    val app_platform: String
)