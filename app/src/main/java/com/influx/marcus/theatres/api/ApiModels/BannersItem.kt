package com.influx.marcus.theatres.api.ApiModels

data class BannersItem(
	val bannerType: Int? = null,
	val imgUrl: String? = null,
	val genre: List<String?>? = null,
	val rating: String? = null,
	val title: String? = null,
	val movieId: Int? = null,
	val tMDBId: String? = null,
	val url: String? = null
)
