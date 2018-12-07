package com.influx.marcus.theatres.api.ApiModels.rewardtransaction


data class RewardTransactionResp(
        val STATUS: Boolean,
        val DATA: DATA
)

data class DATA(
        val points_history: List<PointsHistory>,
        val rewards_history: List<RewardsHistory>,
        val resp_in_sec: Int,
        val message: String = ""
)

data class RewardsHistory(
        val transaction_date: String,
        val transaction_store: String,
        val transaction_type: String,
        val transaction_amount: String
)

data class PointsHistory(
        val transaction_date: String,
        val transaction_store: String,
        val points_earned: String
)