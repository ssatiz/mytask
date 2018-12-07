package com.influx.marcus.theatres.payment

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.blockseat.DATA


/**
 * Created by influx on 02-04-2018.
 */
class TicketTypeAdapter(dataList: DATA, context: Context?) : RecyclerView.Adapter<TicketTypeAdapter.MyViewHolder>() {
    var Item = dataList
    var moviedata: String = ""
    var size = 0
    var mcontext = context

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
            if (Item.receipt.items.get(position).quantity.isNotEmpty()) {
                holder.tvTicketCount.text = Item.receipt.items.get(position).single_price + " X " +
                        Item.receipt.items.get(position).quantity
                holder.tvAmount.text = Item.receipt.items.get(position).price
            }else{
                holder.tvTotal.text  = Item.receipt.items.get(position).price
            }
            holder.tvAgeGroup.text = Item.receipt.items.get(position).name.toUpperCase()


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return Item.receipt.items.size
    }
}
