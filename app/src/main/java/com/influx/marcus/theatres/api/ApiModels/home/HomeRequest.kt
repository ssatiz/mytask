package com.influx.marcus.theatres.api.ApiModels.home


data class HomeRequest(
    val app_platform: String,
    val app_version: String,
    val preference: Preference,
    val state: String
)

data class Preference(
    val cinemas: List<String>,
    val genres: List<String>,
    val languages: List<String>
)