package com.influx.marcus.theatres.api.ApiModels.NearCinemas


data class NearCinemasRequest(
    val tmdbid: String,
    val lat: String,
    val long: String,
    val app_version: String,
    val app_platform: String
)