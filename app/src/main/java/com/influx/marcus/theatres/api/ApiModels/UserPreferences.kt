package com.influx.marcus.theatres.api.ApiModels

class UserPreferences {
    var cinemaList: ArrayList<String> = ArrayList()
    var genreList: ArrayList<String> = ArrayList()
    var languageList: ArrayList<String> = ArrayList()
    var stateCode = ""
    var latitute = ""
    var logitude = ""

    constructor(stateCode: String, latitute: String, logitude: String) {
        this.stateCode = stateCode
        this.latitute = latitute
        this.logitude = logitude
    }

    constructor(){

    }
}