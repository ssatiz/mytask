package com.influx.marcus.theatres.myaccount

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.myaccount.LoyaltyNo
import com.influx.marcus.theatres.myaccount.bookinghistory.RewardDetailActivity
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.FlipListener
import com.squareup.picasso.Picasso


/**
 * Created by influx on 02-04-2018.
 */
class CardAdapter(dataList: LoyaltyNo, cardAdapterlistener: cardAdapterListener, context: Context?) : RecyclerView.Adapter<CardAdapter.MyViewHolder>() {
    var dataType: String = ""
    var LoyaltyItem = dataList
    var mcontext = context
    var cardAdapterlistener = cardAdapterlistener


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //        lateinit var tvCardCount: TextView
        var ivCard: ImageView = view.findViewById(R.id.ivCard)
        var ivQRCode: ImageView = view.findViewById(R.id.ivQRCode)
        var tvName: TextView = view.findViewById(R.id.tvName)
        var tvCardNo: TextView = view.findViewById(R.id.tvCardNo)
        var tvMore: TextView = view.findViewById(R.id.tvMore)
        var tvDelete: TextView = view.findViewById(R.id.tvDelete)
        var loader:LinearLayout = view.findViewById(R.id.llLoader)
    }

    interface cardAdapterListener {
        fun delete(v: View, position: Int, cardInfo: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.inflate_reward_points, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        try {
            holder.loader.visibility = View.VISIBLE
            AppConstants.putString(AppConstants.KEY_LOYALTYCARDNO, LoyaltyItem.card_info.get(0).card_no, mcontext!!)
            holder.tvCardNo.text = LoyaltyItem.card_info.get(position).card_no
            holder.tvName.text = LoyaltyItem.card_info.get(position).card_holder_name

             Picasso.with(mcontext).load(LoyaltyItem.card_info.get(position).card_image).into(holder.ivCard, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {
                        holder.loader.visibility = View.GONE
                        holder.ivCard.visibility = View.VISIBLE }

                    override fun onError() {
                        holder.loader.visibility = View.GONE }
                })
            Picasso.with(mcontext).load(LoyaltyItem.card_info.get(position).qrcode_url).into(holder.ivQRCode, object : com.squareup.picasso.Callback {
                override fun onSuccess() {
                    holder.loader.visibility = View.GONE
                    holder.ivQRCode.visibility = View.VISIBLE }

                override fun onError() {
                    holder.loader.visibility = View.GONE }
            })

            holder.tvMore.setOnClickListener {
                val intent = Intent(mcontext, RewardDetailActivity::class.java)
                intent.putExtra("CardNo", LoyaltyItem.card_info.get(position).card_no)
                mcontext!!.startActivity(intent);
            }
            holder.tvDelete.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    cardAdapterlistener.delete(v!!, position, LoyaltyItem.card_info.get(position).card_no)
                }
            })
            holder.ivCard.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    val mFlipAnimator = ValueAnimator.ofFloat(0f, 1f)
                    mFlipAnimator.duration = 1000
                    mFlipAnimator.addUpdateListener(FlipListener(holder.ivCard, holder.ivQRCode))
                    mFlipAnimator.start()
//                    holder.ivQRCode.visibility = View.VISIBLE
//                    holder.ivCard.visibility = View.GONE
                }
            })
            holder.ivQRCode.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    val mFlipAnimator = ValueAnimator.ofFloat(0f, 1f)
                    mFlipAnimator.duration = 1000
                    mFlipAnimator.addUpdateListener(FlipListener(holder.ivQRCode, holder.ivCard))
                    mFlipAnimator.start()
//                    holder.ivQRCode.visibility = View.GONE
//                    holder.ivCard.visibility = View.VISIBLEs
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
