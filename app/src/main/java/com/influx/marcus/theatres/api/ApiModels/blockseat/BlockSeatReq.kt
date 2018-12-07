package com.influx.marcus.theatres.api.ApiModels.blockseat


data class BlockSeatReq(
        var user_id: String,
        val tmdb_id: String,
        val movie_id: String,
        val ccode: String,
        val cid: String,
        val session_id: String,
        val time_str: String,
        val seat_details: List<SeatDetail>,
        val seats: String,
        val loyaltycards: List<String>,
        val app_version: String,
        val app_platform: String
)

data class SeatDetail(
        val name: String,
        val age_group: String
)

data class BlockSeatResp(
        val STATUS: Boolean,
        val DATA: DATA
)

data class DATA(
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