package com.influx.marcus.theatres.api.ApiModels.preferedlocations.value

import com.influx.marcus.theatres.api.ApiModels.preferedlocations.State
import com.influx.marcus.theatres.api.ApiModels.preferedlocations.Theatres

class values {

}

data class DATA(
        val states: List<State>,
        val theatres: Theatres,
        val genres: List<String>,
        val languages: List<String>,
        var message: String = ""
)
