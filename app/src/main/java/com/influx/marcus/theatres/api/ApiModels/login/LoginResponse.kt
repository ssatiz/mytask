package com.influx.marcus.theatres.api.ApiModels.login


data class LoginResponse(
        val STATUS: Boolean,
        val DATA: LOGINDATA)

data class LOGINDATA(
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
        val loyalty_no: List<LoyaltyNo>,
        val state_code: String,
        val preference: Preference?,
        val new_user: Boolean,
        val message: String)

data class LoyaltyNo(
        val cart_no: String,
        val firstname: String,
        val lastname: String,
        val rewards: String,
        val points: String,
        val default: String
)

data class Preference(
        val cinemas_name: List<String>,
        val cinemas: List<String>,
        val genres: List<String>,
        val languages: List<String>
)