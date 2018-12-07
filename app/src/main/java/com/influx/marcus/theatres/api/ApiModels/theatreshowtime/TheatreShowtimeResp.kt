package com.influx.marcus.theatres.api.ApiModels.theatreshowtime

import java.io.Serializable


data class TheatreShowtimeResp(
        val STATUS: Boolean,
        val DATA: DATa
) : Serializable {
    data class DATa(
            val movies: List<List<MovieInfo>>,
            val dates: List<Date>,
            val message:String,
            val web_view_url: String
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
                    val mname: String,
                    val tmdb_id: String,
                    val mimage: String,
                    val mhimage: String,
                    val cid: String,
                    val miles: String,
                    val lat: String,
                    val long: String,
                    val mdetails: MovieDetails,
                    val exp_data: List<ExpData>
            ) : Serializable {

                data class ExpData(
                        val exp_name: String,
                        val exp_image: String,
                        val exprience_single_logo: String,
                        val exp_desc: String,
                        val more_desc: ExpDesc?,
                        val movie_flag:String,
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
                            val screenname: String,
                            val time_str: String,
                            val is_reserved: Boolean
                    ) : Serializable
                }
            }
        }
    }
}

data class MovieDetails(
        val movie_id: String
):Serializable
