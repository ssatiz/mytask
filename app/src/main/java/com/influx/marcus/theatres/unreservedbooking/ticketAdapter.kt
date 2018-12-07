package com.influx.marcus.theatres.unreservedbooking

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.unreservedbooking.*
import org.jetbrains.anko.toast
import java.text.DecimalFormat
import java.util.concurrent.CopyOnWriteArrayList

class ticketAdapter(dataList: DATA, context: Context?, ticketAdapterlistener: ticketAdapterListener) : RecyclerView.Adapter<ticketAdapter.MyViewHolder>() {
    var Item: List<Tickettype> = dataList.tickettypes
    var maxCount = dataList.maxTickets
    var mcontext = context
    var count = 0
    var itemcount = 0
    var totalCount = 0
    var totalAmount: Float = 0.00f
    var itemAmount: Float = 0.00f
    var ticketarray = CopyOnWriteArrayList<TicketList>()
    var ticketAdapterlistener = ticketAdapterlistener
    var df = DecimalFormat("0.00")

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        lateinit var tvTickeytype: TextView
        lateinit var tvCount: TextView
        lateinit var tvAmount: TextView
        lateinit var ivMinus: ImageView
        lateinit var ivPlus: ImageView

        init {
            tvTickeytype = view.findViewById(R.id.tvTickeytype)
            tvCount = view.findViewById(R.id.tvCount)
            tvAmount = view.findViewById(R.id.tvAmount)
            ivMinus = view.findViewById(R.id.ivMinus)
            ivPlus = view.findViewById(R.id.ivPlus)
        }
    }

    interface ticketAdapterListener {
        fun plus(v: View, position: Int, bookid: String, count: Int, ticketlist: CopyOnWriteArrayList<TicketList>, total: Float)
        fun minus(v: View, position: Int, bookid: String, count: Int, ticketlist: CopyOnWriteArrayList<TicketList>, total: Float)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_unreserved, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        try {
            holder.tvTickeytype.text = Item.get(position).Description
            holder.tvAmount.text = Item.get(position).PriceString
            val maximumCount = Integer.parseInt(maxCount)
            val ticketitem = Item.get(position)
            holder.ivMinus.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    if (holder.tvCount.text.isNotEmpty()) {
                        val countStr = holder.tvCount.text.toString()
                        itemcount = Integer.parseInt(countStr)
                        itemcount = itemcount - 1
                        count = count - 1
                        if (itemcount > 0) {

                            holder.tvCount.setText(itemcount.toString())
                            itemAmount = itemcount * ticketitem.Price
                            val Amount = df.format(itemAmount)
                            holder.tvAmount.setText("$" + Amount.toString())
                            val p = TicketList(ticketitem.TicketTypeCode, itemcount, itemAmount)
                            for (eachticket in ticketarray) {
                                if (eachticket.ticketTypeId.equals(ticketitem.TicketTypeCode)) {
                                    if (eachticket.quantity == 1) {
                                        ticketarray.remove(p)
                                    } else {
                                        eachticket.quantity = itemcount
                                        eachticket.price = itemAmount
                                    }
                                }
                            }
                            totalAmount = 0.00f
                            totalCount = 0
                            for (eachitem in ticketarray) {
                                totalAmount = totalAmount + eachitem.price
                                totalCount = totalCount + eachitem.quantity
                                Log.i("totalvalue", totalAmount.toString())
                            }
                            ticketAdapterlistener.minus(v!!, position, ticketitem.TicketTypeCode, totalCount, ticketarray, totalAmount)
                            Log.i("ticketarraysize", ticketarray.toString())

                        } else {
                            totalAmount = 0.00f
                            totalCount = 0
                            for (eachticket in ticketarray) {
                                if (eachticket.ticketTypeId.equals(ticketitem.TicketTypeCode)) {
                                    if (eachticket.quantity == 1) {
                                        ticketarray.remove(eachticket)
                                    } else {
                                        eachticket.quantity = itemcount
                                        eachticket.price = itemAmount
                                    }
                                }
                            }
                            for (eachitem in ticketarray) {
                                totalAmount = totalAmount + eachitem.price
                                totalCount = totalCount + eachitem.quantity
                                Log.i("totalvalue", totalAmount.toString())
                            }
                            ticketAdapterlistener.minus(v!!, position, ticketitem.TicketTypeCode,
                                    totalCount, ticketarray, totalAmount)
                            Log.i("ticketarraysize", ticketarray.toString())
                            holder.tvCount.setText("")
                            holder.tvAmount.setText(ticketitem.PriceString)
                        }
                    }
                }


            })
            holder.ivPlus.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    if (count > maximumCount - 1) {
                        mcontext?.toast(R.string.max_seats_alreadyselected)
                    } else {
                        if (holder.tvCount.text.isNotEmpty()) {
                            val countStr = holder.tvCount.text.toString()
                            itemcount = Integer.parseInt(countStr)
                            count = count + 1
                            itemcount = itemcount + 1
                            holder.tvCount.setText(itemcount.toString())
                        } else {
                            count = count + 1
                            itemcount = 0
                            itemcount = itemcount + 1
                            holder.tvCount.setText(itemcount.toString())
                        }
                        itemAmount = itemcount * ticketitem.Price
                        val Amount = df.format(itemAmount)
                        holder.tvAmount.setText("$" + Amount.toString())
                        val p = TicketList(ticketitem.TicketTypeCode, itemcount, itemAmount)
                        var isTicketPresent = false
                        if (ticketarray.size > 0) {
                            for (eachticket in ticketarray) {
                                if (eachticket.ticketTypeId.equals(ticketitem.TicketTypeCode)) {
                                    eachticket.quantity = itemcount
                                    eachticket.price = itemAmount
                                    isTicketPresent = true
                                }
                            }
                            if (!isTicketPresent) {
                                ticketarray.add(p)
                            }
                        } else {
                            ticketarray.add(p)
                        }

                        Log.i("ticketarraysize", ticketarray.toString())
                        totalAmount = 0.00f
                        totalCount = 0
                        for (eachitem in ticketarray) {
                            totalAmount = totalAmount + eachitem.price
                            totalCount = totalCount + eachitem.quantity
                            Log.i("totalvalue", totalAmount.toString())
                        }
                        ticketAdapterlistener.plus(v!!, position, ticketitem.TicketTypeCode, totalCount, ticketarray, totalAmount)
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun addItem() {
    }

    override fun getItemCount(): Int {
        return Item.size
    }
}
