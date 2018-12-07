package com.influx.marcus.theatres.api.ApiModels.DeleteSavedCard


data class DeleteSavedCardReq(
    val userid: String,
    val cardid: String,
    val app_version: String,
    val app_platform: String
)

data class DeleteSavedCardResp(
    val STATUS: Boolean,
    val DATA: DATA
)

data class DATA(
    val message: String
)