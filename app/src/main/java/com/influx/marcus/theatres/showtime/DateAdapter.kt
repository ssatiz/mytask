package com.influx.marcus.theatres.showtime

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.showtime.ShowtimeResponse


/**
 * Created by influx on 02-04-2018.
 */
class DateAdapter(dataList: List<ShowtimeResponse.DATa.Date>, context: Context?) : RecyclerView.Adapter<DateAdapter.MyViewHolder>() {
    var dataList: List<ShowtimeResponse.DATa.Date> = dataList
    var mcontext = context


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        lateinit var txtDate: TextView
        lateinit var txtDay: TextView

        init {
            txtDay = view.findViewById(R.id.tvDay)
            txtDate = view.findViewById(R.id.tvDate)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_showtimedate, parent, false)


        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        try {
            val date = dataList.get(position)
            holder.txtDate.setText(date.text + " " + date.number)
            holder.txtDay.setText(date.day)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}