package com.influx.marcus.theatres.api.ApiModels.preferedlocations

import com.influx.marcus.theatres.api.ApiModels.preferedlocations.subval.Data


data class PreferedLocsResp(
        val STATUS: Boolean,
        val DATA: DATA,
        val RESPONSE_IN_SECS: String
)

data class DATA(
        val states: List<State>,
        val theatres: Theatres,
        val genres: List<String>,
        val languages: List<String>,
        var message: String = "",
        val default_radius: String,
        val current_radius: String
)

data class State(
        val state: String,
        val scode: String
)

data class Theatres(
        val cities: List<City>
)

data class City(
        val cityname: String,
        val data: List<Data>
)


data class PreferedLocsReq(
        val authToken: String,
        val state: String,
        val lat: String,
        val long: String
)

data class genredata(
        var name: String,
        var favourite: Boolean = false
)

data class langdata(
        var name: String,
        var favourite: Boolean = false
)
