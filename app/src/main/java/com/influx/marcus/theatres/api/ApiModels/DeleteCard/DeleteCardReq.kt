package com.influx.marcus.theatres.api.ApiModels.DeleteCard


data class DeleteCardReq(
    val userid: String,
    val loyalty_card: String,
    val app_version: String,
    val app_platform: String
)