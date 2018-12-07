package com.influx.marcus.theatres.api.ApiModels

data class ComingSoonItem(
	val performanceCount: Int? = null,
	val releaseDate: String? = null,
	val director: String? = null,
	val imageUrl: String? = null,
	val length: Int? = null,
	val rating: String? = null,
	val dDDFlag: Boolean? = null,
	val language: String? = null,
	val movieId: String? = null,
	val trailerUrl: String? = null,
	val experiences: List<ExperiencesItem?>? = null,
	val longName: String? = null,
	val movieSlug: String? = null,
	val rottenTomatoesRate: String? = null,
	val cinemaId: String? = null,
	val movieCode: String? = null,
	val theatreCode: Int? = null,
	val name: String? = null,
	val genre: List<Any?>? = null,
	val starring: String? = null,
	val shortName: String? = null,
	val tMDBId: String? = null,
	val ratingReasons: String? = null,
	val desc: String? = null
)
