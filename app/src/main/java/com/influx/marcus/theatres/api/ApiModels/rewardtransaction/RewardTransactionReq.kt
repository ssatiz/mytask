package com.influx.marcus.theatres.api.ApiModels.rewardtransaction


data class RewardTransactionReq(
    val card_number: String,
    val app_version: String,
    val app_platform: String
)