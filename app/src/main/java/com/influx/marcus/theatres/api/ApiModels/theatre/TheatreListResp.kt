package com.influx.marcus.theatres.api.ApiModels.theatre



data class TheatreListResp(
    val STATUS: Boolean,
    val DATA: List<DATA>
)

data class DATA(
        var message:String="",
    val theatre_code: String,
    val theatre_id: String,
    val name: String,
    val state_code: String,
    val latitude: String,
    val longitude: String,
    val image_url: String,
    val address: String,
    val web_view_url: String
)
