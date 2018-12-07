package com.influx.marcus.theatres.api.ApiModels.rewardcarddetail


data class RewardCardDetailResp(
    val STATUS: Boolean,
    val DATA: DATA
)

data class DATA(
    val banner_url: String,
    val point_balance: PointBalance,
    val reward_balance: Double,
    val rewards_in_text: String,
    val card_number: String,
    val notes: String,
    val terms_and_conditions_url: String,
    val resp_in_sec: Int,
    val message: String
)

data class PointBalance(
    val points: String,
    val points_threshold: String
)