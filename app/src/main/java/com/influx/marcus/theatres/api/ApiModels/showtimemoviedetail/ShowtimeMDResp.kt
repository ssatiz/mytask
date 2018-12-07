package com.influx.marcus.theatres.api.ApiModels.showtimemoviedetail


data class ShowtimeMDResp(
        val STATUS: Boolean,
        val DATA: DATA
)

data class DATA(
        val tMDBId: String,
        val cinema_id: String,
        val theatre_code: String,
        val movie_id: String,
        val movie_code: String,
        val long_name: String,
        val short_name: String,
        val name: String,
        val movie_slug: String,
        val desc: String,
        val length: String,
        val rating: String,
        val ratingReasons: String,
        val dDDFlag: Boolean,
        val starring: List<String>,
        val genre: List<String>,
        val language: String,
        val director: List<String>,
        val rottenTomatoes_rate: String,
        val trailerUrl: String,
        val releaseDate: String,
        val image_url: String,
        val experiences: Experiences,
        var h_image_url: String="",
        val caution: String?
)

data class Experiences(
        val name: String,
        val image: String,
        val description: String
)


data class ShowtimeMDReq(
        val state: String,
        val tmdb_id: String
)