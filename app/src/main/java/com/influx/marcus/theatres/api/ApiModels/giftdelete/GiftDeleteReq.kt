package com.influx.marcus.theatres.api.ApiModels.giftdelete


data class GiftDeleteReq(
    val app_platform: String,
    val app_version: String,
    val cardNumber: String,
    val userId: String
)

data class GiftDeleteResp(
    val STATUS: Boolean,
    val DATA: DATA
)

data class DATA(
    val banner: String,
    val title: String,
    val card_info: List<CardInfo>
)

data class CardInfo(
    val card_name: String,
    val card_no: String,
    val pin: String,
    val qrcode_url:String,
    val card_image: String
)