package com.influx.marcus.theatres.api.ApiModels.home

import java.io.Serializable


data class HomeResponse(
        val STATUS: Boolean,
        val DATA: DATA
)

data class DATA(
        val banners: List<Banner>,
        val movies: Movies,
        var message: String
)

data class Movies(
        val now_showing: List<NowShowing>,
        val coming_soon: List<ComingSoon>,
        val event_cinema: List<EventCinema>)

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
        val dDDFlag: String,
        val starring: List<String>,
        val genre: List<String>,
        val language: String,
        val director: List<String>,
        var rottenTomatoes_rate: String ="",
        val trailerUrl: String,
        val releaseDate: String,
        val releaseDateStr: String,
        val image_url: String,
        var h_image_url: String="",
    //    val experiences: Experience,
        val caution: String?
) : Serializable

data class ComingSoon(
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
        val dDDFlag: String,
        val starring: List<String>,
        val genre: List<String>,
        val language: String,
        val director: List<String>,
        var rottenTomatoes_rate: String ="",
        val trailerUrl: String,
        val releaseDate: String,
        val releaseDateStr: String,
        val image_url: String,
        val experiences: Experience,
        var h_image_url: String="",
        val caution: String?
// "caution": "The film features flashing lights which may affect viewers who are susceptible to photosensitive epilepsy or other photosensitives."
) : Serializable

data class Experience(
        val name: String,
        val image: String,
        val description: String
) : Serializable

data class EventCinema(
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
        val dDDFlag: String,
        val starring: List<String>,
        val genre: List<String>,
        val language: String,
        val director: List<String>,
        var rottenTomatoes_rate: String ="",
        val trailerUrl: String,
        val releaseDate: String,
        val releaseDateStr: String,
        val image_url: String,
        val experiences: Experience,
        var h_image_url: String="",
        val caution: String?
) : Serializable

data class Banner(
        val banner_type: String,
        val img_url: String,
        val title: String,
        val genre: List<String>,
        val movie_id: String,
        val tMDBId: String,
        val rating: String,
        val url: String
)