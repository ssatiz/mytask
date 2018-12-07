package com.influx.marcus.theatres.showtime

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.showtime.ShowtimeResponse

class ShowSessionAdapter(val context: Context, val sessionDataList: List<ShowtimeResponse.DATa.MovieInfo.Cinemas.ExpData.SessionData>) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: View?
        val vh: ShowVH
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.row_showtimeindividual, parent, false)
            vh = ShowVH(view)
            view.tag = vh
        } else {
            view = convertView
            vh = view.tag as ShowVH
        }
        vh.tvShow.text = sessionDataList.get(position).time

        return view!!
    }

    override fun getItem(position: Int): Any {
        return sessionDataList.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return sessionDataList.size
    }
}

private class ShowVH(item: View) {
    val tvShow = item.findViewById<TextView>(R.id.tvShowTime)
}