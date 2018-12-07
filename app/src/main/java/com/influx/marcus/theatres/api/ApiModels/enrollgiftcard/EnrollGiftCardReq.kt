package com.influx.marcus.theatres.api.ApiModels.enrollgiftcard


data class EnrollGiftCardReq(
        val cardNumber: String,
        val pin: String,
        val userId: String,
        val app_version: String,
        val app_platform: String
)