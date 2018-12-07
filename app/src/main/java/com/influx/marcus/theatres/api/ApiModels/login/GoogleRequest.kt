package com.influx.marcus.theatres.api.ApiModels.login


data class GoogleRequest(
        val google_id: String,
        val firstname: String,
        val lastname: String,
        val state_code: String,
        val preference: Preference,
        val app_version: String,
        val app_platform: String
) {

    data class Preference(
            val cinemas: List<String>,
            val genres: List<String>,
            val languages: List<String>
    )
}