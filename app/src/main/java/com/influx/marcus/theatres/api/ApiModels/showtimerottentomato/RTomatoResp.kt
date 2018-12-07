package com.influx.marcus.theatres.api.ApiModels.showtimerottentomato


data class RTomatoResp(
    val STATUS: Boolean,
    val DATA: DATA
)

data class DATA(
    val ratings: String
)


data class RtomatoReq(
    val moviename: String,
    val app_version: String,
    val app_platform: String
)