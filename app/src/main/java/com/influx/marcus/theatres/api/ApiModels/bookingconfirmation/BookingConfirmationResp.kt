package com.influx.marcus.theatres.api.ApiModels.bookingconfirmation


data class BookingConfirmationResp(
        val STATUS: Boolean,
        val DATA: CONFDATA
)

data class CONFDATA(
        val movie_title: String,
        val movie_image: String,
        val cinema_name: String,
        val show_time: String,
        val show_date: String,
        val failure: String,
        val ticketCount: String,
        val screen_name: String,
        val experience_img: String,
        val experience_title: String,
        val exprience_single_logo: String,
        val seat_info: String,
        val booking_id: String,
        val qrcode: String,
        val qrcode_text: String,
        val message: String,
        val messageDetail: String,
        val booking_data: BOOKING_DATA
)

data class BOOKING_DATA(
        val movie_title: String,
        val movie_image: String,
        val cinema_name: String,
        val show_time: String,
        val show_date: String,
        val failure: String,
        val ticketCount: String,
        val screen_name: String,
        val experience_img: String,
        val experience_title: String,
        val exprience_single_logo: String,
        val seat_info: String,
        val booking_id: String,
        val qrcode: String,
        val qrcode_text: String,
        val message: String,
        val messageDetail: String

)

data class Result(
        val message: String,
        val error_sub_code: String
)

