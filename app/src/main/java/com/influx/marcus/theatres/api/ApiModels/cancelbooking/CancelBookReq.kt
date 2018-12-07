package com.influx.marcus.theatres.api.ApiModels.cancelbooking


data class CancelBookReq(
        val theatre_code: String,
        val cinema_id: String,
        val sale_id: String,
        val app_version: String,
        val app_platform: String
)


data class CancelBookResp(
        val STATUS: Boolean,
        val DATA: DATA
)

data class DATA(
        val receipt: Receipt,
        val saleid: String,
        val key: Key,
        val timeout: String,
        val responseTime: String
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