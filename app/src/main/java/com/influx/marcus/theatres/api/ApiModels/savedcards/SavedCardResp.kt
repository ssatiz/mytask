package com.influx.marcus.theatres.api.ApiModels.savedcards


data class SavedCardResp(
    val STATUS: Boolean,
    val DATA: DATA
)

data class DATA(
    val savedCards: List<SavedCard>
)

data class SavedCard(
    val card_id: String,
    val card_type: String,
    val card_no: String,
    val card_month: String,
    val card_year: String,
    val first_name: String,
    val last_name: String,
    val masked_no: String
)