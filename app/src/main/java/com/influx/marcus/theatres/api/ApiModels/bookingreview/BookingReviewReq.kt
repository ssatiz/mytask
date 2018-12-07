package com.influx.marcus.theatres.api.ApiModels.bookingreview


data class BookingReviewReq(
        val ccode: String,
        val cid: String,
        val session_id: String,
        val time_str: String,
        val seats: String,
        val loyaltycards: List<String>,
        val sale_id: String
)


data class BookingReviewResponse(
        val status: Boolean,
        val data: Data
)

data class Data(
        val receipt: Receipt,
        val saleid: String,
        val key: Key,
        val timeout: String,
        val responseTime: String,
        val message: String
)

data class Receipt(
        val items: List<Item>,
        val total: Double,
        val totalincents: Int,
        val purchasedDateTime: String
)

data class Item(
        val name: String,
        val price: String,
        val priceincents: String,
        val quantity: String,
        val single_price: String,
        val single_price_cents: String
)


data class Key(
        val modulusHexString: String,
        val exponentHexString: String
)
