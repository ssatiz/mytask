package com.influx.marcus.theatres.api.ApiModels.filter


data class FilterResp(
        val STATUS: Boolean,
        val DATA: DATA
)

data class DATA(
        val show_dates: Int,
        val cinemas: List<Cinema>,
        val genres: ArrayList<String>,
        val languages: ArrayList<String>,
        val message: String
)

data class Cinema(
        val theatercode: String,
        val name: String,
        val cinema_id: String,
        val state: String,
        val miles_str: String,
        var isSelect: Boolean = false,
        val is_preferred : Boolean,
        val full_address : String

)

data class genredata(
        var name: String,
        var favourite: Boolean = false
)
