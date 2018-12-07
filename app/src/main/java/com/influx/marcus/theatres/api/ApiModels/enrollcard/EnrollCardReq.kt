package com.influx.marcus.theatres.api.ApiModels.enrollcard


data class EnrollCardReq(
    val userid: String,
    val loyalty_no: String,
    val cardinfo: String
)