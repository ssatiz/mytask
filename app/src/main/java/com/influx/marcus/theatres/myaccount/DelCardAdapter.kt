package com.influx.marcus.theatres.myaccount

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.DeleteCard.DELDATA
import com.influx.marcus.theatres.myaccount.bookinghistory.RewardDetailActivity
import com.squareup.picasso.Picasso


/**
 * Created by influx on 02-04-2018.
 */
class DelCardAdapter(dataList: DELDATA, cardAdapterlistener: cardAdapterListener, context: Context?) : RecyclerView.Adapter<DelCardAdapter.MyViewHolder>() {
    var dataType: String = ""
    var LoyaltyItem = dataList
    var mcontext = context
    var cardAdapterlistener = cardAdapterlistener


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        lateinit var tvCardCount: TextView
        lateinit var ivCard: ImageView
        lateinit var tvName: TextView
        lateinit var tvCardNo: TextView
        lateinit var tvMore: TextView
        lateinit var tvDelete :TextView

        init {
            tvCardNo = view.findViewById(R.id.tvCardNo)
            tvCardCount = view.findViewById(R.id.tvCardCount)
            ivCard = view.findViewById(R.id.ivCard)
            tvName = view.findViewById(R.id.tvName)
            tvMore = view.findViewById(R.id.tvMore)
            tvDelete = view.findViewById(R.id.tvDelete)

        }
    }


    interface cardAdapterListener {
        fun delete(v: View, position: Int, cardInfo: String)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_reward_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        try {

            holder.tvCardNo.text = LoyaltyItem.card_info.get(position).card_no
            holder.tvCardCount.text = LoyaltyItem.card_info.get(position).card_name
            holder.tvName.text = LoyaltyItem.card_info.get(position).card_holder_name
            Picasso.with(mcontext).load(LoyaltyItem.card_info.get(position).card_image).into(holder.ivCard)
            holder.tvMore.setOnClickListener{
                val intent = Intent(mcontext, RewardDetailActivity::class.java)
                intent.putExtra("CardNo", LoyaltyItem.card_info.get(position).card_no)
                mcontext!!.startActivity(intent);
            }
            holder.tvDelete.setOnClickListener(object :View.OnClickListener{
                override fun onClick(v: View?) {
                    cardAdapterlistener.delete(v!!, position, LoyaltyItem.card_info.get(position).card_no)

                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun getItemCount(): Int {
        return LoyaltyItem.card_info.size
    }
}
