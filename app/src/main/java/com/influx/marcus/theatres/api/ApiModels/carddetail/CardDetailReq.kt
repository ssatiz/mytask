package com.influx.marcus.theatres.api.ApiModels.carddetail


data class CardDetailReq(
    val card_number: String,
    val app_version: String,
    val app_platform: String
)