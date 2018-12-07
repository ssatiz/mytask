package com.influx.marcus.theatres.api.ApiModels.upgradeloyalty


data class UpgradeLoyaltyReq(
    val user_id: String,
    val firstname: String,
    val lastname: String,
    val email: String,
    val dob: String,
    val mobile: String,
    val address: String,
    val city: String,
    val state: String,
    val zipcode: String,
    val app_version: String,
    val app_platform: String
)