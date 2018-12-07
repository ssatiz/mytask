package com.influx.marcus.theatres.api.ApiModels.signup


data class SignupReq(
        val firstname: String,
        val lastname: String,
        val email: String,
        val password: String,
        val mobile: String,
        val rewards: String,
        val newsletter : String,
        val gender: String,
        val dob: String,
        val address: String,
        val city: String,
        val state: String,
        val zipcode: String,
        val login_source: String,
        val social_id: String,
        val preference: Preference,
        val state_code: String,
        val app_version: String,
        val app_platform: String
)

