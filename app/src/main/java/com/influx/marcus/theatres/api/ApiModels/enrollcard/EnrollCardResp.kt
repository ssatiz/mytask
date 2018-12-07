package com.influx.marcus.theatres.api.ApiModels.enrollcard


data class EnrollCardResp(
        val STATUS: Boolean,
        val DATA: DATA
)

data class DATA(
        val success_msg: String,
        val messageDetail: String,
        val message: String
)