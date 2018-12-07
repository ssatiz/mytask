package com.influx.marcus.theatres.api.ApiModels.updatePref

data class Preference(
        val cinemas: List<String>,
        val genres: List<String>,
        val languages: List<String>
)