package com.influx.marcus.theatres.moviefilter

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.filter.Cinema
import com.influx.marcus.theatres.utils.AppConstants
import com.squareup.picasso.Picasso


/**
 * Created by influx on 02-04-2018.
 */
class LocationAdapter(dataList: List<Cinema>, context: Context?,
                      selectedCinemaListener: SelectedCinemaListener) :
        RecyclerView.Adapter<LocationAdapter.MyViewHolder>() {
    var CinemasLocList = dataList
    var mcontext = context
    var selectedCinemaListener = selectedCinemaListener

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var favImg: ImageView
        var cinemaName: TextView
        var tvMiles: TextView
        var clLocation: ConstraintLayout

        init {
            favImg = view.findViewById(R.id.select)
            cinemaName = view.findViewById(R.id.tvCinema)
            tvMiles = view.findViewById(R.id.tvMiles)
            clLocation = view.findViewById(R.id.clLocation)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_location_filter, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var cinemaItem = CinemasLocList.get(position)
        holder.cinemaName.text = cinemaItem.name
        if( cinemaItem.miles_str.isNotEmpty()&&cinemaItem.miles_str.isNotBlank()) {
            holder.tvMiles.text = " - " + cinemaItem.miles_str
        }


        if (cinemaItem.isSelect) {// selected item
            if (CinemasLocList.get(position).is_preferred == true) {
                Picasso.with(mcontext!!).load(R.drawable.whitefilledheart).into(holder.favImg)
            } else {
                Picasso.with(mcontext!!).load(R.drawable.whiteborderheart).into(holder.favImg)
            }
            holder.cinemaName.setTextColor(ContextCompat.getColor(mcontext!!, R.color.white))
            holder.clLocation.setBackgroundColor(ContextCompat.getColor(mcontext!!, R.color.marcus_red))
        } else {
            if (cinemaItem.is_preferred == true) {
                Picasso.with(mcontext!!).load(R.drawable.redfilledheart).into(holder.favImg)
            } else {
                Picasso.with(mcontext!!).load(R.drawable.redborderheart).into(holder.favImg)
            }
            holder.cinemaName.setTextColor(ContextCompat.getColor(mcontext!!, R.color.grey_light))
            holder.clLocation.setBackgroundColor(ContextCompat.getColor(mcontext!!, R.color.black))
        }
        if (cinemaItem.theatercode in AppConstants.selectedCinmastoFilter) {
            selectedCinemaListener.select(CinemasLocList.get(position).cinema_id)

            if (CinemasLocList.get(position).is_preferred == true) {
                Picasso.with(mcontext!!).load(R.drawable.whitefilledheart).into(holder.favImg)
            } else {
                Picasso.with(mcontext!!).load(R.drawable.whiteborderheart).into(holder.favImg)
            }
            holder.cinemaName.setTextColor(ContextCompat.getColor(mcontext!!, R.color.white))
            holder.clLocation.setBackgroundColor(ContextCompat.getColor(mcontext!!, R.color.marcus_red))

        }


        holder.clLocation.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (CinemasLocList.get(position).isSelect == false) {

                    CinemasLocList.get(position).isSelect = true
                    if (CinemasLocList.get(position).is_preferred == true) {
                        Picasso.with(mcontext!!).load(R.drawable.whitefilledheart).into(holder.favImg)
                    } else {
                        Picasso.with(mcontext!!).load(R.drawable.whiteborderheart).into(holder.favImg)
                    }
                    if (CinemasLocList.get(position).theatercode in AppConstants.selectedCinmastoFilter) {
                    } else {
                        AppConstants.selectedCinmastoFilter.add(CinemasLocList.get(position).theatercode)

                    }
                    selectedCinemaListener.select(CinemasLocList.get(position).cinema_id)
                    holder.cinemaName.setTextColor(ContextCompat.getColor(mcontext!!, R.color.white))
                    holder.clLocation.setBackgroundColor(ContextCompat.getColor(mcontext!!, R.color.marcus_red))
                } else {

                    CinemasLocList.get(position).isSelect = false
                    if (CinemasLocList.get(position).theatercode in AppConstants.selectedCinmastoFilter) {
                        AppConstants.selectedCinmastoFilter.remove(CinemasLocList.get(position).theatercode)
                    }

                    if (CinemasLocList.get(position).is_preferred == true) {
                        Picasso.with(mcontext!!).load(R.drawable.redfilledheart).into(holder.favImg)
                    } else {
                        Picasso.with(mcontext!!).load(R.drawable.redborderheart).into(holder.favImg)
                    }
                    selectedCinemaListener.unselect(AppConstants.selectedCinmastoFilter.size)
                    holder.cinemaName.setTextColor(ContextCompat.getColor(mcontext!!, R.color.grey_light))
                    holder.clLocation.setBackgroundColor(ContextCompat.getColor(mcontext!!, R.color.black))
                }
            }
        })

    }

    override fun getItemCount(): Int {
        return CinemasLocList.size
    }

    interface SelectedCinemaListener {
        fun select(cinemaId: String)
        fun unselect(size: Int)
    }

}

