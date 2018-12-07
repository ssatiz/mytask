package com.influx.marcus.theatres.api.ApiModels.unreservedbooking


data class UnreservedReq(
    val ccode: String,
    val cid: String,
    val session_id: String,
    val time_str: String
)