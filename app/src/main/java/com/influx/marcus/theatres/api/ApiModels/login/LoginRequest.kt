package com.influx.marcus.theatres.api.ApiModels.login


data class LoginRequest(
        val email: String,
        val password: String,
        val app_version: String,
        val app_platform: String
)