package com.influx.marcus.theatres.api.ApiModels.DeleteCard


data class DeleteCardResp(
        val STATUS: Boolean,
        val DATA: DELDATA
)

data class DELDATA(
        val banner: String,
        val title: String,
        val card_info: List<CardInfo>,
        val message: String
)

data class CardInfo(
        val card_name: String,
        val card_no: String,
        val card_holder_name: String,
        val card_image: String,
        val qrcode_url: String
)