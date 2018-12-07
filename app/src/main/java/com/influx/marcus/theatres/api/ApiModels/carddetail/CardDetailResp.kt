package com.influx.marcus.theatres.api.ApiModels.carddetail

data class CardDetailResp(
    val STATUS: Boolean,
    val DATA: DATA
)

data class DATA(
    val card_number: String,
    val points: String,
    val dollars: String,
    val dollars_in_text: String,
    val responseInSec: Int
)