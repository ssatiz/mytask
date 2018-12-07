package com.influx.marcus.theatres.api.ApiModels.unreservedbooking

data class LockUnreservedResp(
        val STATUS: Boolean,
        val DATA: UNRESERVEDDATA
)

data class UNRESERVEDDATA(
        val receipt: Receipt,
        val order_id: String,
        val sale_id: String,
        val key: Key,
        val timeout: String,
        val responseTime: String,

        var message: String = ""
)

data class Receipt(
        val items: List<Item>,
        val total: String,
        val totalincents: String,
        val seatinfo: String,
        val purchasedDateTime: String
)

data class Item(
        val name: String,
        val single_price: String,
        val price: String,
        val priceincents: String,
        val single_price_cents: String,
        val quantity: String
)

data class Key(
        val modulusHexString: String,
        val exponentHexString: String
)