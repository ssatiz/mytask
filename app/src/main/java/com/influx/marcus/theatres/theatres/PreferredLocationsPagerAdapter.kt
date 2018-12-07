package com.influx.marcus.theatres.theatres

import android.content.Context
import android.content.Intent
import android.support.constraint.ConstraintLayout
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.utils.AppConstants
import com.squareup.picasso.Picasso


/**
 * Created by influx on 02-04-2018.
 */
class PreferredLocationsPagerAdapter(val locs: List<Any>, var context: Context?) : PagerAdapter() {


    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as View
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = LayoutInflater.from(container.context)
                .inflate(R.layout.adapter_theatre_viewpager, container, false)

        var tvCinemaName: TextView = itemView.findViewById(R.id.tvCinemaName)
        var tvAddress: TextView = itemView.findViewById(R.id.tvAddress)
        var ivTheatre: ImageView = itemView.findViewById(R.id.ivCinemaLoc)
        val clViewPager: ConstraintLayout = itemView.findViewById(R.id.clViewPager)

        val data = locs.get(position)

        if (data is com.influx.marcus.theatres.api.ApiModels.OurTheatresGPS.Preference) {
            tvAddress.setText(data.address)
            tvCinemaName.setText(data.name.toUpperCase())
            clViewPager.setOnClickListener {
                val i = Intent(context, TheatreShowTimeActivity::class.java)
                AppConstants.putString(AppConstants.KEY_THEATRECODE, data.theatre_code, context!!)
                AppConstants.putString(AppConstants.KEY_THEATREIMAGE, data.image_url, context!!)
                AppConstants.putString(AppConstants.KEY_THEATRENAME, data.name, context!!)
                context!!.startActivity(i)
            }
            if (data.image_url != null && data.image_url.isNotBlank()) {
                Picasso.with(context).load(data.image_url).into(ivTheatre)
            }
        } else if (data is com.influx.marcus.theatres.api.ApiModels.OurTheatresNonGPS.Preference) {
            tvAddress.setText(data.address)
            tvCinemaName.setText(data.name.toUpperCase())
            clViewPager.setOnClickListener {
                val i = Intent(context, TheatreShowTimeActivity::class.java)
                AppConstants.putString(AppConstants.KEY_THEATRECODE, data.theatre_code, context!!)
                AppConstants.putString(AppConstants.KEY_THEATREIMAGE, data.image_url, context!!)
                AppConstants.putString(AppConstants.KEY_THEATRENAME, data.name, context!!)
                context!!.startActivity(i)
            }
            if (data.image_url != null && data.image_url.isNotBlank()) {
                Picasso.with(context).load(data.image_url).resize(230, 70).into(ivTheatre)
            }
        }
        container.addView(itemView)
        return itemView
    }

    override fun getCount(): Int {
        return locs.size
    }

    override fun destroyItem(parent: ViewGroup, position: Int, `object`: Any) {
        // Remove the view from view group specified position
        parent.removeView(`object` as View)
    }

}