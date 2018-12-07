package com.influx.marcus.theatres.api.ApiModels.guest


data class GuestRegResp(
        val STATUS: Boolean,
        val DATA: DATA
)

data class DATA(
        val userid: String,
        val guest: String,
        var message: String = "",
        val email: String,
        val mobile: String,
        val firstname: String,
        val lastname: String
)

data class GuestRegReq(
        val firstname: String,
        val lastname: String,
        val email: String,
        val mobile: String,
        val preference: Preference,
        val state_code: String,
        val app_version: String,
        val app_platform: String
)

data class Preference(
        val cinemas: List<String>,
        val genres: List<String>,
        val languages: List<String>
)