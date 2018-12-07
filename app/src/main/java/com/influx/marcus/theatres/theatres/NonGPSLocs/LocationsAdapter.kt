package com.influx.marcus.theatres.theatres.NonGPSLocs

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.OurTheatresNonGPS.Theatre
import com.influx.marcus.theatres.api.ApiModels.theatreshowtime.TheatreShowtimeRequest
import com.influx.marcus.theatres.theatres.OurTheatres
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.UtilsDialog

class LocationsAdapter(val context: Context, val theatres: List<Theatre>, val fetchTheatresListener: OurTheatres.fetchTheatreShowTime) :
        RecyclerView.Adapter<LocationsAdapter.LocsVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocsVH {
        val rowView = LayoutInflater.from(context).inflate(R.layout.row_cinemalocation, parent, false)
        return LocsVH(rowView)
    }

    override fun getItemCount(): Int {
        return theatres.size
    }

    override fun onBindViewHolder(holder: LocsVH, position: Int) {
        val data = theatres.get(position)
        holder.tvCinemaName.text = data.name
        holder.tvCinemaAddress.text = data.address1
        holder.tvCinemaAddressNextLine.text = data.address2

        holder.location_adapter_layout.setOnClickListener {
            AppConstants.putString(AppConstants.KEY_THEATRECODE, data.code, context!!)
            AppConstants.putString(AppConstants.KEY_THEATREIMAGE, data.image_url, context!!)
            AppConstants.putString(AppConstants.KEY_THEATRENAME, data.name, context!!)
            if (UtilsDialog.isNetworkStatusAvialable(context)) {
                UtilsDialog.showProgressDialog(context, "")
                val req = TheatreShowtimeRequest(
                        data.code,
                        AppConstants.APP_VERSION,
                        AppConstants.APP_PLATFORM)
                fetchTheatresListener.fetchTheareShowtimeByCode(req)
            }
        }
    }

    class LocsVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCinemaName = itemView.findViewById<TextView>(R.id.tvCinemaName)
        val tvCinemaAddress = itemView.findViewById<TextView>(R.id.tvCinemaAddress)
        val tvCinemaAddressNextLine = itemView.findViewById<TextView>(R.id.tvCinemaAddressNextLine)
        val viewLine = itemView.findViewById<View>(R.id.viewLine)
        val location_adapter_layout = itemView.findViewById<View>(R.id.location_adapter_layout)
    }
}