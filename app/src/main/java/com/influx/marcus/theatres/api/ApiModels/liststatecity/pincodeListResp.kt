package com.influx.marcus.theatres.api.ApiModels.liststatecity


data class pincodeListResp(
    val STATUS: Boolean,
    val DATA: List<PINCODEDATA>
)

data class PINCODEDATA(
    val zip: String
)