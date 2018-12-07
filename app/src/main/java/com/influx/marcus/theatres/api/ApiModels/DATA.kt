package com.influx.marcus.theatres.api.ApiModels

data class DATA(
	val movies: Movies? = null,
	val banners: List<BannersItem?>? = null
)
