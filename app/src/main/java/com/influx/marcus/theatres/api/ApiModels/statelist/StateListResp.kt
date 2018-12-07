package com.influx.marcus.theatres.api.ApiModels.statelist


data class StateListResp(

        val STATUS: Boolean,
        val DATA: List<DATA>
)

data class DATA(
        val state: String,
        val scode: String
)