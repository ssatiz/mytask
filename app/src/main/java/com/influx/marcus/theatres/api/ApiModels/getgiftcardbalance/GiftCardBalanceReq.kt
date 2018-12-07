package com.influx.marcus.theatres.api.ApiModels.getgiftcardbalance


data class GiftCardBalanceReq(
        val cardNumber: String,
        val pin: String,
        val app_version: String,
        val app_platform: String
)