package com.influx.marcus.theatres.api.ApiModels.enrollgiftcard


data class EnrollGiftCardResp(
        val STATUS: Boolean,
        val DATA: DATA
)

data class DATA(
        val balance: String,
        val currency: String,
        val secureAccountNumber: String,
        val expirationDate: String,
        val card_image: String,
        val message: String,
        val messageDetail: String,
        val qrcode_url: String,
        val successMessage: String

)