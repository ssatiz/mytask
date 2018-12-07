package com.influx.marcus.theatres.api.ApiModels.cancelbooking


data class CancelBookingResp(
    val STATUS: Boolean,
    val DATA: List<Any>
)