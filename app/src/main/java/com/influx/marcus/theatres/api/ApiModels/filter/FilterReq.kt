package com.influx.marcus.theatres.api.ApiModels.filter


data class FilterReq(
    val preferred_cinemas: List<String>,
    val lat: String,
    val long: String,
    val app_version: String,
    val app_platform: String
)