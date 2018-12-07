package com.influx.marcus.theatres.api.ApiModels.paymentcardlist


data class PaymentCardListResp(
        val STATUS: Boolean,
        val DATA: DATA
)

data class PaymentMethod(
        val payment_method_id: Int,
        val name: String
)
data class RewardDetails(
        val name: String,
        val card_details: List<CardDetail>
)
data class CardDetail(
        val card_number: String,
        val dollars: String,
        val points: String,
        val dollars_in_text: String,
        val responseInSec: Int
)
data class DATA(
    val reward_details: RewardDetails,
    val payment_methods: List<PaymentMethod>,
    val saved_cards: List<SavedCard>,
     val gift_cards: List<GiftCard>,
    val allowed_card_types: List<String>,
    val message:String)

data class GiftCard(
    val card_no: String,
    val pin: String
)
data class SavedCard(
        val card_id: String,
        val card_type: String,
        val card_no: String,
        val card_month: String,
        val card_year: String,
        val first_name: String,
        val last_name: String,
        val masked_no: String,
        val card_image: String,
        var isSelect: Boolean = false,
        var isVisible: Boolean = false
)


