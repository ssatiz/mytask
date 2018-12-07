package com.influx.marcus.theatres.seatlayout

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.showtime.ShowtimeResponse


/**
 * Created by influx on 02-04-2018.
 */
class TimePagerAdapter(var dataList: ArrayList<ShowtimeResponse.DATa.MovieInfo.Cinemas.ExpData.SessionData>,
                       var context: Context?) : PagerAdapter() {

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as View
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = LayoutInflater.from(container.context)
                .inflate(R.layout.adapter_seattimedate, container, false)

        var txtTime: TextView = itemView.findViewById(R.id.tvTime)

        val date = dataList.get(position)
        txtTime.setText(date.time)
        container.addView(itemView)
        return itemView
    }

    override fun getCount(): Int {
        return dataList.size
    }

    override fun destroyItem(parent: ViewGroup, position: Int, `object`: Any) {
        // Remove the view from view group specified position
        parent.removeView(`object` as View)
    }


}
