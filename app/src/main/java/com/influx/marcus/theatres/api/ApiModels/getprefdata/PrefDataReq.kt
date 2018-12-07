package com.influx.marcus.theatres.api.ApiModels.getpref


data class PrefDataReq(
    val state_code: String,
    val lat: String,
    val long: String,
    val app_version: String,
    val app_platform: String
)