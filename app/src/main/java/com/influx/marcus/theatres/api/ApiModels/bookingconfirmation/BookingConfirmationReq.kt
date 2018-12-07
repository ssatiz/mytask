package com.influx.marcus.theatres.api.ApiModels.bookingconfirmation


data class BookingConfirmationReq(
        val user_id: String,
        val order_id: String,
        val payment_method_id: String,
        val loyalty_card: String,
        val loyalty_pay: String,
        val first_name: String,
        val last_name: String,
        val email_address: String,
        val cardnum: String,
        val expmm: String,
        val expyy: String,
        val cvv: String,
        val avs: String,
        val cardtype: String,
        val save_card: String,
        val gift_card: String,
        val gift_card_pin:String,
        val gift_card_pay:String,
        val app_version: String,
        val app_platform: String
)


data class resp(
        val user_id: String,
        val order_id: String,
        val payment_method_id: String,
        val loyalty_card: String,
        val loyalty_pay: String,
        val first_name: String,
        val last_name: String,
        val email_address: String,
        val cardnum: String,
        val expmm: String,
        val expyy: String,
        val cvv: String,
        val avs: String,
        val cardtype: String,
        val app_version: String,
        val app_platform: String
)