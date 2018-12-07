package com.influx.marcus.theatres.api.ApiModels.upgradeloyalty


data class UpgradeLoyaltyResp(
    val STATUS: Boolean,
    val DATA: DATA
)

data class DATA(
    val userid: String,
    val firstname: String,
    val lastname: String,
    val email: String,
    val mobile: String,
    val gender: String,
    val address: String,
    val city: String,
    val state: String,
    val country: String,
    val loyalty_img: String,
    val loyalty_msg : String,
    val loyalty_no: List<LoyaltyNo>,
    val state_code: String,
    val preference: Preference,
    val message: String

)

data class Preference(
    val cinemas_name: List<String>,
    val cinemas: List<String>,
    val genres: List<String>,
    val languages: List<String>
)

data class LoyaltyNo(
    val cart_no: String,
    val firstname: String,
    val lastname: String,
    val rewards: String,
    val points: String,
    val default: String
)