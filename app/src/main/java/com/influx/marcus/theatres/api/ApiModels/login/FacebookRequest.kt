package com.influx.marcus.theatres.api.ApiModels.login

import java.io.Serializable


data class FacebookRequest(
        val facebook_id: String,
        val email: String,
        val firstname: String,
        val lastname: String,
        val state_code: String,
        val preference: Preference,
        val app_version: String,
        val app_platform: String
) : Serializable {

    data class Preference(
            val cinemas: List<String>,
            val genres: List<String>,
            val languages: List<String>
    ) : Serializable
}