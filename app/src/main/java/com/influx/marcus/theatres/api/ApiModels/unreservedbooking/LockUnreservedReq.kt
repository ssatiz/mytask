package com.influx.marcus.theatres.api.ApiModels.unreservedbooking


data class LockUnreservedReq(
    var user_id: String,
    var tmdb_id: String,
    val movie_id: String,
    val ccode: String,
    val cid: String,
    val session_id: String,
    val time_str: String,
    val tickets: List<Ticket>,
    val loyaltycards: List<String>,
    val app_version: String,
    val app_platform: String
)

data class Ticket(
    val ticketTypeId: String,
    val quantity: Int
)