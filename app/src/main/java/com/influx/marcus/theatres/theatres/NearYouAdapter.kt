package com.influx.marcus.theatres.theatres

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.OurTheatresGPS.AllTheatre

class NearYouAdapter(val context: Context, val theatres: List<AllTheatre>) :
        RecyclerView.Adapter<NearYouAdapter.LocsVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocsVH {
        val rowView = LayoutInflater.from(context).inflate(R.layout.adapter_theatre_nearyou, parent, false)
        return LocsVH(rowView)
    }

    override fun getItemCount(): Int {
        return theatres.size
    }

    override fun onBindViewHolder(holder: LocsVH, position: Int) {
        val data = theatres.get(position)
        holder.tvCinemaName.text = data.name
        if(data.address2.isEmpty() || data.address2.equals(" ", ignoreCase = true) || data.address2 == null){
            holder.tvCinemaAddress.text = data.address1
        }else{
            holder.tvCinemaAddress.text = data.address1+","
        }

        holder.tvCinemaAddressTwo.text = data.address2
        if(data.miles.equals("0.00", ignoreCase = true)){
            holder.tvmiles.visibility = View.GONE
        }else{
            holder.tvmiles.visibility = View.VISIBLE
            holder.tvmiles.text = data.miles_str
        }

    }

    class LocsVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCinemaName = itemView.findViewById<TextView>(R.id.tvCinemaName)
        val tvCinemaAddress = itemView.findViewById<TextView>(R.id.tvCinemaAddress)
        val tvCinemaAddressTwo = itemView.findViewById<TextView>(R.id.tvCinemaAddressTwo)
        val tvmiles = itemView.findViewById<TextView>(R.id.tvmiles)

    }
}