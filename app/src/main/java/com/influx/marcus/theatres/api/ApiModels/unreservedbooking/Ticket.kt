package com.influx.marcus.theatres.api.ApiModels.unreservedbooking

data class TicketArray(
        val tickets: List<TicketList>
)

data class TicketList(
        var ticketTypeId: String,
        var quantity: Int,
        var price: Float
)