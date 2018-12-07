package com.influx.marcus.theatres.theatres

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.influx.marcus.theatres.R
import com.squareup.picasso.Picasso

class PreferredLocsAdapter(val context: Context, val locsList: List<Any>) : RecyclerView.Adapter<PreferredLocsAdapter.LocsVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocsVH {
        val rowView = LayoutInflater.from(context)
                .inflate(R.layout.row_preferredlocs, parent, false)
        return LocsVH(rowView)
    }

    override fun getItemCount(): Int {
        return locsList.size
    }

    override fun onBindViewHolder(holder: LocsVH, position: Int) {
        val data = locsList.get(position)
        if (data is com.influx.marcus.theatres.api.ApiModels.OurTheatresGPS.Preference) {
            if (data.image_url.isNotBlank()) {
              //  Picasso.with(context).load(data.image_url).into(holder.ivCinema)
                Picasso.with(context)
                        .load(data.image_url)
                        .into(holder.ivCinema, object : com.squareup.picasso.Callback {
                            override fun onError() {
                                holder.loader.visibility = View.GONE
                            }

                            override fun onSuccess() {
                                holder.loader.visibility = View.GONE
                                holder.ivCinema.visibility = View.VISIBLE
                            }
                        })
            }
            holder.tvCinemaName.text = data.name
            holder.tvAddress.text = data.address
        } else if (data is com.influx.marcus.theatres.api.ApiModels.OurTheatresNonGPS.Preference) {
            if (data.image_url.isNotBlank()) {
                Picasso.with(context)
                        .load(data.image_url)
                        .into(holder.ivCinema, object : com.squareup.picasso.Callback {
                            override fun onError() {
                                holder.loader.visibility = View.GONE
                            }

                            override fun onSuccess() {
                                holder.loader.visibility = View.GONE
                                holder.ivCinema.visibility = View.VISIBLE
                            }
                        })
            }
            holder.tvCinemaName.text = data.name
            holder.tvAddress.text = data.address
        }
    }


    class LocsVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCinemaName = itemView.findViewById<TextView>(R.id.tvCinemaName)
        val tvAddress = itemView.findViewById<TextView>(R.id.tvAddress)
        val ivCinema = itemView.findViewById<ImageView>(R.id.ivCinemaLoc)
        val loader = itemView.findViewById<LinearLayout>(R.id.llLoader)
    }
}