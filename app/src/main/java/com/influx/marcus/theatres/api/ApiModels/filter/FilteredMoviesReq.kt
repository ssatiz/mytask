package com.influx.marcus.theatres.api.ApiModels.filter


data class FilteredMoviesReq(
    val date: String,
    val cinemas: List<String>,
    val genres: List<Any>,
    val languages: List<Any>,
    val app_version: String,
    val app_platform: String
)