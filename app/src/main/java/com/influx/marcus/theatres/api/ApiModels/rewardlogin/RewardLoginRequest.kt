package com.influx.marcus.theatres.api.ApiModels.rewardlogin

data class RewardLoginRequest (
        val loyalty_no: String,
        val cardinfo: String,
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