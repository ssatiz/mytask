package com.influx.marcus.theatres.api.ApiModels.preferedlocations.subval

class subval {
}
data class Data(
        val code: String,
        val name: String,
        val short_name: String,
        val address1: String,
        val address2: String,
        val City: String,
        val State: String,
        val state_code: String,
        val Zip: String,
        val full_address: String,
        val miles: Double,
        val miles_str: String,
        val latitude: String,
        val longitude: String,
        var isPrefered:Boolean = false
)
