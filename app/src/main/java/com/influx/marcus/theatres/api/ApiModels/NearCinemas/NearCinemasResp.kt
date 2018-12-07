package com.influx.marcus.theatres.api.ApiModels.NearCinemas

import java.io.Serializable

data class NearCinemasResp(
        val STATUS: Boolean,
        val DATA: DATa
) : Serializable {

    data class DATa(
            val movies: List<List<MovieInfo>>,
            val dates: List<Date>
    ) : Serializable {

        data class Date(
                val bdate: String,
                val text: String,
                val number: String,
                val day: String
        ) : Serializable


        data class MovieInfo(
                val show_date: String?,
                val cinemas: Cinemas?,
                var isHeader: Boolean = false,
                var isPreferred: Boolean = false
        ) : Serializable {

            data class Cinemas(
                    val cname: String,
                    val ccode: String,
                    val cid: String,
                    val miles: String,
                    val lat: String,
                    val long: String,
                    val exp_data: List<ExpData>
            ) : Serializable {

                data class ExpData(
                        val exp_name: String,
                        val exp_image: String,
                        val exp_desc: String,
                        val more_desc: ExpDesc?,
                        val maintenance: MaintainanceData?,
                        val session_data: List<SessionData>
                ) : Serializable {

                    data class ExpDesc(
                            val icon: String?,
                            val desc: String?
                    ) : Serializable

                    data class MaintainanceData(
                            val icon: String?,
                            val desc: String?
                    ) : Serializable

                    data class SessionData(
                            val session_id: String,
                            val screen: String,
                            val bdate: String,
                            val text: String,
                            val number: String,
                            val day: String,
                            val time: String,
                            val screenname:String,
                            val time_str: String,
                            val is_reserved:Boolean
                    ) : Serializable
                }
            }
        }
    }
}