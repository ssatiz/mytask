package com.influx.marcus.theatres.api.ApiModels.savedcards


data class SavedCardReq(
    val userid: String,
    val app_version: String,
    val app_platform: String
)