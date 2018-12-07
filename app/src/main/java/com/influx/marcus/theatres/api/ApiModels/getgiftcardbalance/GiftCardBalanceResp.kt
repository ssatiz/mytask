package com.influx.marcus.theatres.api.ApiModels.getgiftcardbalance


data class GiftCardBalanceResp(
        val STATUS: Boolean,
        val DATA: DATA
)

data class DATA(
        val balance: String,
        val balanceInString: String
        )