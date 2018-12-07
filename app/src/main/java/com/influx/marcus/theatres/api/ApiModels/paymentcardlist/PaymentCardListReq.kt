package com.influx.marcus.theatres.api.ApiModels.paymentcardlist

data class PaymentCardListReq(
        val user_id: String,
        val app_version: String,
        val app_platform: String)
