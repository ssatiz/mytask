package com.influx.marcus.theatres.api.ApiModels.rewardcarddetail


data class RewardCardDetailReq(
    val card_number: String,
    val app_version: String,
    val app_platform: String
)