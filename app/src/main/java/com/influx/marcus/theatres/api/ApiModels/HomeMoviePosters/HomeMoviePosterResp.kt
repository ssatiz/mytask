package com.influx.marcus.theatres.api.ApiModels.HomeMoviePosters

data class HomeMoviePosterResp(
    val DATA: DATA,
    val STATUS: Boolean
)

data class DATA(
    val banners: List<Banner>,
    val movies: Movies
)

data class Movies(
    val coming_soon: List<ComingSoon>,
    val event_cinema: List<EventCinema>,
    val now_showing: List<NowShowing>
)

data class NowShowing(
    val caution: String,
    val cinema_id: String,
    val count: Int,
    val genre: List<String>,
    val image_url: String,
    val long_name: String,
    val movie_code: String,
    val movie_id: String,
    val movie_slug: String,
    val name: String,
    val short_name: String,
    val rating: String,
    val tMDBId: String,
    val theatre_code: String
)

data class ComingSoon(
    val caution: String,
    val cinema_id: String,
    val genre: List<String>,
    val image_url: String,
    val long_name: String,
    val movie_code: String,
    val movie_id: String,
    val movie_slug: String,
    val releaseDateStr: String,
    val name: String,
    val short_name: String,
    val rating: String,
    val tMDBId: String,
    val theatre_code: String
)

data class EventCinema(
    val caution: String,
    val cinema_id: String,
    val genre: List<String>,
    val image_url: String,
    val long_name: String,
    val movie_code: String,
    val movie_id: String,
    val movie_slug: String,
    val name: String,
    val short_name: String,
    val tMDBId: String,
    val theatre_code: String,
    val rating: String
)

data class Banner(
    val banner_type: String,
    val genre: List<String>,
    val img_url: String,
    val movie_id: String,
    val order: String,
    val rating: String,
    val tMDBId: String,
    val title: String,
    val url: String
)