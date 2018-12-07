package com.influx.marcus.theatres.api.ApiModels.myaccount


data class MyAccountReq(
    val userid: String,
    val app_version: String,
    val app_platform: String
)