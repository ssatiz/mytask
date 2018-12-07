package com.influx.marcus.theatres.api.ApiModels.OurTheatresGPS

data class OTGPSResp(
    val DATA: DATA,
    val STATUS: Boolean
)

data class DATA(
    val allTheatres: List<AllTheatre>,
    val default_radius: String,
    val preferences: List<Preference>
)

data class AllTheatre(
    val City: String,
    val State: String,
    val Zip: String,
    val address1: String,
    val address2: String,
    val code: String,
    val full_address: String,
    val latitude: String,
    val longitude: String,
    val miles: String,
    val miles_str: String,
    val name: String,
    val short_name: String,
    val state_code: String,
    val image_url: String
)

data class Preference(
    val address: String,
    val full_address_string: String,
    val image_url: String,
    val latitude: String,
    val longitude: String,
    val name: String,
    val state: String,
    val state_code: String,
    val theatre_code: String,
    val theatre_id: String,
    val web_view_url: String
)

data class OTGPSReq(
    val app_platform: String,
    val app_version: String,
    val lat: String,
    val long: String,
    val preference: List<String>
)