package com.influx.marcus.theatres.api.ApiModels.liststatecity


data class statelistResp(
    val STATUS: Boolean,
    val DATA: List<DATA>
)

data class DATA(
    val state: String,
    val scode: String
)