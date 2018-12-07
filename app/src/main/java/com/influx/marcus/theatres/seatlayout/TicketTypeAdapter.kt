package com.influx.marcus.theatres.seatlayout

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.seatlayout.Tickettype


/**
 * Created by influx on 02-04-2018.
 */
class TicketTypeAdapter(var context: Context, var ticktArr: List<Tickettype>) : RecyclerView.Adapter<TicketTypeAdapter.MyViewHolder>() {


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        lateinit var tvPrice: TextView
        lateinit var tvTicketType: TextView

        init {
            tvPrice = view.findViewById(R.id.tvPrice)
            tvTicketType = view.findViewById(R.id.tvTicketType)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_tickettype, parent, false)


        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        try {
            val date = ticktArr.get(position)
            //val pricewithtax = date.Price.toFloat() + date.Tax.toFloat()
            val pricewithtax = date.Price.toFloat()
            holder.tvPrice.text = "$ " + pricewithtax+"0"
            holder.tvTicketType.setText(date.Description)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return ticktArr.size
    }
}