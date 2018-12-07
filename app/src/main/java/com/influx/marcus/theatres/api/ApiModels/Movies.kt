package com.influx.marcus.theatres.api.ApiModels

data class Movies(
	val comingSoon: List<ComingSoonItem?>? = null,
	val eventCinema: List<EventCinemaItem?>? = null,
	val nowShowing: List<NowShowingItem?>? = null
)
