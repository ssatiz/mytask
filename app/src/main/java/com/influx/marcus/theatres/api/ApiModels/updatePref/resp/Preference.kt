package com.influx.marcus.theatres.api.ApiModels.updatePref.resp

data class Preference(
        val cinemas_name: List<String>,
        val cinemas: List<String>,
        val genres: List<String>,
        val languages: List<String>
)