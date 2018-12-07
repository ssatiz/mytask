package com.influx.marcus.theatres.api.ApiModels.rewardscardslist


data class RewardsCardsListResp(
    val STATUS: Boolean,
    val DATA: CARDLISTDATA
)

data class CARDLISTDATA(
    val banner: String,
    val title: String,
    val card_info: List<CardInfo>
)

data class CardInfo(
    val card_name: String,
    val card_no: String,
    val card_holder_name: String,
    val card_image: String,
    val qrcode_url: String
)