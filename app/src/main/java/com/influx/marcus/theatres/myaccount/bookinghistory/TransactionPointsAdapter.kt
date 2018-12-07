package com.influx.marcus.theatres.myaccount.bookinghistory

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.rewardtransaction.PointsHistory

class TransactionPointsAdapter(dataList: List<PointsHistory>, context: Context?) :
        RecyclerView.Adapter<TransactionPointsAdapter.MyViewHolder>() {
    var PointsList = dataList
    var size = PointsList.size
    var mcontext = context

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvDate: TextView
        lateinit var tvPointsEarned: TextView
        lateinit var tvTheatres: TextView
        lateinit var clTransaction : ConstraintLayout

        init {
            tvDate = view.findViewById(R.id.tvDate)
            tvPointsEarned = view.findViewById(R.id.tvPointsEarned)
            tvTheatres = view.findViewById(R.id.tvTheatres)
            clTransaction = view.findViewById(R.id.clTransaction)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_points_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        try {
            holder.tvPointsEarned.text = PointsList.get(position).points_earned
            holder.tvTheatres.text = PointsList.get(position).transaction_store
            holder.tvDate.text = PointsList.get(position).transaction_date
            if(position %2 == 1)
            {
                holder.clTransaction.setBackgroundColor(mcontext!!.resources.getColor(R.color.grey));
            }
            else
            {
                holder.clTransaction.setBackgroundColor(mcontext!!.resources.getColor(R.color.black));
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun getItemCount(): Int {
        return size
    }
}
