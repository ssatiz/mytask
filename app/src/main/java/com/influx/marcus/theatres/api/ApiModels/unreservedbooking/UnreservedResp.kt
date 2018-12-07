package com.influx.marcus.theatres.api.ApiModels.unreservedbooking


data class UnreservedResp(
    val STATUS: Boolean,
    val DATA: DATA
)

data class DATA(
    val maxTickets: String,
    val tickettypes: List<Tickettype>,
    var message:String=""
)

data class Tickettype(
    val TicketTypeCode: String,
    val Description: String,
    val sortOrder: String,
    val Price: Float,
    val PriceString: String,
    val PriceInCents: String,
    val isValidforOffer: Boolean,
    val IsSoldOut: Boolean,
    val Tax: String,
    val Qty: String
)