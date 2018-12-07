package com.influx.marcus.theatres.myaccount.bookinghistory

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.rewardtransaction.RewardsHistory

class TransactionRewardsAdapter(dataList: List<RewardsHistory>, context: Context?) :
        RecyclerView.Adapter<TransactionRewardsAdapter.MyViewHolder>() {
    var RewardList = dataList
    var size = RewardList.size
    var mcontext = context

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvDateVal: TextView
        var tvTheatresVal: TextView
        var tvTransaction: TextView
        var tvAmount: TextView
       lateinit var clRewards: ConstraintLayout

        init {
            tvDateVal = view.findViewById(R.id.tvDateVal)
            tvTheatresVal = view.findViewById(R.id.tvTheatresVal)
            tvTransaction = view.findViewById(R.id.tvTransaction)
            tvAmount = view.findViewById(R.id.tvAmount)
            clRewards = view.findViewById(R.id.clRewards)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_rewards_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        try {
            holder.tvTheatresVal.text = RewardList.get(position).transaction_store
            holder.tvAmount.text = RewardList.get(position).transaction_amount
            holder.tvDateVal.text = RewardList.get(position).transaction_date
            holder.tvTransaction.text = RewardList.get(position).transaction_type
            if(position %2 == 1)
            {
                holder.clRewards.setBackgroundColor(mcontext!!.resources.getColor(R.color.grey));
            }
            else
            {
                holder.clRewards.setBackgroundColor(mcontext!!.resources.getColor(R.color.black));
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun getItemCount(): Int {
        return size
    }
}
