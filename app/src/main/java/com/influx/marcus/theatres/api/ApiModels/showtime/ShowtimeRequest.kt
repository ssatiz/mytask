package com.influx.marcus.theatres.api.ApiModels.showtime


data class ShowtimeRequest(
        val state: String,
        val lat: String,
        val long: String,
        val movie_code: String,
        val tmdbid: String,
        val cinemas: List<String>
)

