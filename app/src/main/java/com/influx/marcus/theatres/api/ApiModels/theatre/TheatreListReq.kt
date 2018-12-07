package com.influx.marcus.theatres.api.ApiModels.theatre


data class TheatreListReq(
    val state: String,
    val app_version: String,
    val app_platform: String
)