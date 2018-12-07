package com.influx.marcus.theatres.api.ApiModels.seatlayout


data class SeatResp(
        val STATUS: Boolean,
        val DATA: DATA
)

data class DATA(
        val arealist: Arealist,
        val message:String
)

data class Arealist(
        val AreaCode: String,
        val AreaDescription: String,
        val maxTickets: String,
        val tickettypes: List<Tickettype>,
        val rowlist: List<Rowlist>,
        val WheelchairMsg: String,
        val CampanionMsg: String
)

data class Tickettype(
        val TicketTypeCode: String,
        val Description: String,
        val sortOrder: String,
        val Price: String,
        val PriceInCents: String,
        val isValidforOffer: Boolean,
        val IsSoldOut: Boolean,
        val Tax: String,
        val Qty: String
)

data class Rowlist(
        val PhysicalName: String,
        val seatlist: List<Seatlist>
)

data class Seatlist(
        val Name: String,
        val SeatStyle: String,
        val Status: String,
        val RowNumber: String,
        val ColumnNumber: String,
        val AreaCode: String,
        val VistaPassingValue: String,
        var isSelect: Boolean = false,
        var ticketTypeCode: String = "",
        var ticketTypeName: String = "",
        var priceWithTax: String = "",
        var PriceInCents : String ,
        var isApplyToAllSeat: Boolean = false
)

data class SeatReq(
        val ccode: String,
        val cid: String,
        val session_id: String,
        val time_str: String

)