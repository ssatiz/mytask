package com.influx.marcus.theatres.api.ApiModels.myaccount

import java.io.Serializable


data class MyAccountResp(
        val STATUS: Boolean,
        val DATA: DATA
) : Serializable

data class DATA(
        val userid: String,
        val firstname: String,
        val lastname: String,
        val email: String,
        val mobile: String,
        val img_url: String,
        val gender: String,
        val dob: String,
        val address: String,
        val city: String,
        val state: String,
        val zipcode: String,
        val screenname: String,
        val country: String,
        val decrpt_pwd: String,
        val facebook: String,
        val google: String,
        val newsletter: String,
        val marcusOnfacebook: String,
        val loyalty_no: LoyaltyNo,
        val gift_cards: GiftCards,
        val booking_history: BookingHistory,
        val movie_youlike: List<MovieYoulikeItem>,
        val state_code: String,
        val preference: Preference,
        val message:String
) : Serializable

data class LoyaltyNo(
        val banner: String,
        val title: String,
        val card_info: List<CardInfo>
) : Serializable

data class GiftCards(
        val banner: String,
        val title: String,
        val card_info: MutableList<GiftCardInfo>
) : Serializable

data class CardInfo(
        val card_name: String,
        val card_no: String,
        val card_holder_name: String,
        val card_image: String,
        val qrcode_url: String
) : Serializable

data class GiftCardInfo(
        var balance: String,
        val card_name: String,
        val card_no: String,
        val pin: String,
        val qrcode_url:String,
        val card_image: String
) : Serializable

data class BookingHistory(
        val coming_up: List<ComingUp>,
        val last_seen: List<LastSeen>
) : Serializable

data class ComingUp(
        val id: String,
        val movie_id: String,
        val moviename: String,
        val movieimage: String,
        val cinemaname: String,
        val experience_img: String,
        val experience_name: String,
        val showdatetime: String,
        val screenname: String,
        val booking_id:String,
        val daystogo: String,
        val showdate: String,
        val showtime: String,
        val seatinfo: String,
        val ticket_total_amount: String,
        val ticketdescription: Ticketdescription,
        val ticket_service_charge: String,
        val totalamount: String,
        val qrcode_url: String,
        val exprience_single_logo:String
) : Serializable

data class Ticketdescription(
        val items: List<Item>,
        val total: String,
        val totalincents: String,
        val purchasedDateTime: String
) : Serializable

data class Item(
        val name: String,
        val single_price: String,
        val price: String,
        val priceincents: String,
        val single_price_cents: String,
        val quantity: String
) : Serializable

data class LastSeen(
        val id: String,
        val movie_id: String,
        val moviename: String,
        val cinemaname: String,
        val experience_img: String,
        val experience_name: String,
        val exprience_single_logo: String,
        val showdate: String,
        val showtime: String,
        val seatinfo: String,
        val ticket_total_amount: String,
        val ticketdescription: Ticketdescription,
        val ticket_service_charge: String,
        val totalamount: String
) : Serializable

data class Preference(
        val cinemas: List<String>,
        val genres: List<String>,
        val languages: List<String>
) : Serializable

data class MovieYoulikeItem(
        val tMDBId: String,
        val cinema_id: String,
        val theatre_code: String,
        val movie_id: String,
        val movie_code: String,
        val long_name: String,
        val short_name: String,
        val name: String,
        val movie_slug: String,
        val desc: String,
        val length: String,
        val rating: String,
        val ratingReasons: String,
        val dDDFlag: Boolean,
        val starring: List<String>,
        val genre: List<String>,
        val language: String,
        val director: List<Any>,
        var rottenTomatoes_rate: String = "",
        val trailerUrl: String,
        val releaseDate: String,
        val releaseDateStr: String,
        val image_url: String,
        var h_image_url: String="",
        val experiences: Experiences,
        val caution: String
) : Serializable

data class Experiences(
        val name: String,
        val image: String,
        val description: String
) : Serializable

/*
data class MyAccountResp(
    val STATUS: Boolean,
    val DATA: DATA
)

data class DATA(
    val userid: String,
    val firstname: String,
    val lastname: String,
    val email: String,
    val mobile: String,
    val img_url: String,
    val gender: String,
    val dob: String,
    val address: String,
    val city: String,
    val state: String,
    val zipcode: String,
    val country: String,
    val decrpt_pwd: String,
    val facebook: String,
    val google: String,
    val newsletter: String,
    val marcusOnfacebook: String,
    val loyalty_no: List<LoyaltyNo>,
    val booking_history: BookingHistory,
    val movie_youlike: List<MovieYoulikeItem>,
    val state_code: String,
    val preference: Preference
)

data class BookingHistory(
    val coming_up: List<ComingUp>,
    val last_seen: List<LastSeen>
)

data class ComingUp(
    val id: String,
    val movie_id: String,
    val moviename: String,
    val movieimage: String,
    val cinemaname: String,
    val experience_img: String,
    val showdatetime: String,
    val daystogo: String,
    val showdate: String,
    val showtime: String,
    val seatinfo: String,
    val ticket_total_amount: String,
    val ticketdescription: Ticketdescription,
    val ticket_service_charge: String,
    val totalamount: String
)

data class Ticketdescription(
    val items: List<Item>,
    val subTotal: String,
    val tax: String,
    val total: String,
    val purchasedDateTime: String
)

data class Item(
    val name: String,
    val price: String,
    val tax: String,
    val quantity: String,
    val total: String,
    val sortOrder: String,
    val canBeRemoved: String
)

data class LastSeen(
    val id: String,
    val movie_id: String,
    val moviename: String,
    val cinemaname: String,
    val experience_img: String,
    val showdate: String,
    val showtime: String,
    val seatinfo: String,
    val ticket_total_amount: String,
    val ticketdescription: Ticketdescription,
    val ticket_service_charge: String,
    val totalamount: String
)

data class Preference(
    val cinemas: List<String>,
    val genres: List<String>,
    val languages: List<String>
)

data class LoyaltyNo(
    val cart_no: String,
    val default: String
)

data class MovieYoulikeItem(
    val tMDBId: String,
    val cinema_id: String,
    val theatre_code: String,
    val movie_id: String,
    val movie_code: String,
    val long_name: String,
    val short_name: String,
    val name: String,
    val movie_slug: String,
    val desc: String,
    val length: String,
    val rating: String,
    val ratingReasons: String,
    val dDDFlag: Boolean,
    val starring: List<String>,
    val genre: List<String>,
    val language: String,
    val director: List<Any>,
    val rottenTomatoes_rate: String,
    val trailerUrl: String,
    val releaseDate: String,
    val image_url: String,
    val experiences: Experience,
    val caution: String
)

data class Experience(
    val name: String,
    val image: String,
    val description: String
)*/
