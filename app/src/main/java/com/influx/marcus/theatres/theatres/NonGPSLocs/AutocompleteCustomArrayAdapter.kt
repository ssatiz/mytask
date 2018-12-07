package com.influx.marcus.theatres.theatres.NonGPSLocs

import android.content.Context
import android.content.Intent
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiInterface
import com.influx.marcus.theatres.api.ApiModels.OurTheatresNonGPS.Theatre
import com.influx.marcus.theatres.api.ApiModels.theatreshowtime.TheatreShowtimeRequest
import com.influx.marcus.theatres.api.ApiModels.theatreshowtime.TheatreShowtimeResp
import com.influx.marcus.theatres.api.RestClient
import com.influx.marcus.theatres.theatres.TheatreShowTimeActivity
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.UtilsDialog
import org.jetbrains.anko.alert
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AutocompleteCustomArrayAdapter(val context: Context, val data: ArrayList<Theatre>) :
        RecyclerView.Adapter<AutocompleteCustomArrayAdapter.LocsVH>() {
    private val webApi: ApiInterface = RestClient.getApiClient()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocsVH {
        val rowView = LayoutInflater.from(context).inflate(R.layout.adapter_theatre_search, parent, false)
        return LocsVH(rowView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: LocsVH, position: Int) {

        val theatreData = data!!.get(position)

        holder.textViewItem.setText(theatreData.name)
        holder.textViewItemsub.setText(theatreData.State)

        holder.clSeach.setOnClickListener {
            AppConstants.putString(AppConstants.KEY_THEATRECODE, theatreData.code, context!!)
            AppConstants.putString(AppConstants.KEY_THEATREIMAGE, theatreData.image_url, context!!)
            AppConstants.putString(AppConstants.KEY_THEATRENAME, theatreData.name, context!!)
            if (UtilsDialog.isNetworkStatusAvialable(context)) {
                UtilsDialog.showProgressDialog(context, "")
                val req = TheatreShowtimeRequest(
                        theatreData.code,
                        AppConstants.APP_VERSION,
                        AppConstants.APP_PLATFORM)
                getTheatreDetailsList(req)
            }
        }
    }

    /**
     * api call to fetch theatreshowtime
     */
    fun getTheatreDetailsList(req: TheatreShowtimeRequest) {
        UtilsDialog.showProgressDialog(context, "")
        val theatreListCall: Call<TheatreShowtimeResp> = webApi.getTheatreDetails(
                AppConstants.AUTHORISATION_HEADER,
                req
        )
        theatreListCall.enqueue(object : Callback<TheatreShowtimeResp> {
            override fun onFailure(call: Call<TheatreShowtimeResp>, t: Throwable) {
                UtilsDialog.hideProgress()

            }

            override fun onResponse(call: Call<TheatreShowtimeResp>,
                                    response: Response<TheatreShowtimeResp>) {
                UtilsDialog.hideProgress()
                if (response.isSuccessful) {

                        val theatreShowtimeResponse = response.body()
                        if(theatreShowtimeResponse!!.STATUS){
                        AppConstants.putObject(AppConstants.KEY_THEATRESHOWTIMEOBJ, theatreShowtimeResponse!!, context)
                            val i = Intent(context, TheatreShowTimeActivity::class.java)
                            AppConstants.putString(AppConstants.KEY_THEATRESHOWTIME, "theatrelist", context)
                            context.startActivity(i)
                        }
                        else {
                            context.alert("No Showtimes Available", "Marcus Theatres") {
                                positiveButton("OK") {
                                    it.dismiss()
                                }
                            }.show()
                        }
                    }

                 else {
                    context.alert("No Showtimes Available", "Marcus Theatres") {
                        positiveButton("OK") {
                            it.dismiss()
                        }
                    }.show()
                }
            }
        })
    }


    class LocsVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val textViewItem = itemView!!.findViewById(R.id.tvSearchCinemaname) as TextView
        val textViewItemsub = itemView.findViewById(R.id.tvSearchCinemaLocation) as TextView
        val clSeach = itemView.findViewById(R.id.theatre_search_layout) as ConstraintLayout
    }
}
