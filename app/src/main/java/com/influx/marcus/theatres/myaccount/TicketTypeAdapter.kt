package com.influx.marcus.theatres.myaccount

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.myaccount.LastSeen
import com.influx.marcus.theatres.api.ApiModels.myaccount.Ticketdescription


/**
 * Created by influx on 02-04-2018.
 */
class TicketTypeAdapter(dataList: Ticketdescription, context: Context?) : RecyclerView.Adapter<TicketTypeAdapter.MyViewHolder>() {
    var dataType: String = ""
     var TicketItem = dataList
    lateinit var lastseenTicket: List<LastSeen>

    var size = TicketItem.items.size
    var mcontext = context

   /* init {
        if (dataList.size > 0) {
            if (dataList.get(0) is ComingUp) {
                dataType = "comingup"
                comingupTicket = dataList as List<ComingUp>
                size = comingupTicket.size
            } else if (dataList.get(0) is LastSeen) {
                dataType = "lastseen"
                lastseenTicket = dataList as List<LastSeen>
                size = lastseenTicket.size
            }
        }
    }*/

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvAgeGroup: TextView
        lateinit var tvTicketCount: TextView
        lateinit var genre: TextView
        lateinit var tvAmount: TextView
        lateinit var tvTotal: TextView


        init {
            tvAgeGroup = view.findViewById(R.id.tvAgeGroup)
            tvTicketCount = view.findViewById(R.id.ticketCount)
            tvAmount = view.findViewById(R.id.txtAmount)
            tvTotal = view.findViewById(R.id.tvTotal)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_review_ticket, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        try {
            if (TicketItem.items.get(position).quantity.isNotEmpty()) {
                holder.tvTicketCount.text =TicketItem.items.get(position).single_price + " X " +
                        TicketItem.items.get(position).quantity
                holder.tvAmount.text = TicketItem.items.get(position).price
            }else{
                holder.tvTotal.text  = TicketItem.items.get(position).price
            }
            holder.tvAgeGroup.text = TicketItem.items.get(position).name.toUpperCase()


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return size
    }
}
