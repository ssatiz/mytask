package com.influx.marcus.theatres.api.ApiModels.liststatecity


data class cityListResp(
    val STATUS: Boolean,
    val DATA: List<CITYDATA>
)

data class CITYDATA(
    val place: String
)