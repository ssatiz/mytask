package com.influx.marcus.theatres.api.ApiModels.updatePref

data class UpdatePrefReq(
        val user_id: String,
        val preference: Preference,
        val state_code: String,
        val app_version: String,
        val app_platform: String
)