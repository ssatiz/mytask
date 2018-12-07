package com.influx.marcus.theatres.api.ApiModels.filter


data class FilteredMoviesResp(
    val STATUS: Boolean,
    val DATA: FILTERDATA
)

data class FILTERDATA(
    val movies: Movies
)

data class Movies(
    val now_showing: List<NowShowing>,
    val coming_soon: List<Any>,
    val event_cinema: List<Any>
)

data class NowShowing(
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
    val starring: List<String>,
    val genre: List<String>,
    val subtitled: Boolean,
    val dubbed: Boolean,
    val language: String,
    val director: List<String>,
    val trailerUrl: String,
    val releaseDate: String,
    val releaseDateStr: String,
    val image_url: String,
    val h_image_url: String,
    val Distributor: String,
    val caution: String
)