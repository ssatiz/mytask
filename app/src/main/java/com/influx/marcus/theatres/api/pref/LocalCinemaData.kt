package com.influx.marcus.theatres.api.pref

data class LocalCinemaData(
        val cinemas:MutableList<CinemaDetail>
)
data class CinemaDetail(
        val cinemaName:String,
        val cinemaCode:String
)