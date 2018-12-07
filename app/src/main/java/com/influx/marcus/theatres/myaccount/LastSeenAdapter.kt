package com.influx.marcus.theatres.myaccount

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.myaccount.LastSeen
import com.squareup.picasso.Picasso


/**
 * Created by influx on 02-04-2018.
 */
class LastSeenAdapter(dataList: List<LastSeen>, context: Context?) : RecyclerView.Adapter<LastSeenAdapter.MyViewHolder>() {
    //  var Item = dataList
    var lastSeenData = dataList
    var mcontext = context

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //  var tvAgeGroup: TextView
        lateinit var rvTicketType: RecyclerView
        lateinit var genre: TextView
        lateinit var tvMveName: TextView
        lateinit var ivUpArrow: ImageView
        lateinit var ivExperience: ImageView
        lateinit var tvCinemaName: TextView
        lateinit var tvDate: TextView
        lateinit var showtime: TextView
        lateinit var tvSelectedSeats: TextView
        lateinit var tvTotalAmount: TextView
        lateinit var rrTicketDetailsLayout: RelativeLayout
        lateinit var constraintLayout: ConstraintLayout
        lateinit var ivBottomNotch: ImageView
        lateinit var tvExperienceName: TextView

        init {
            rvTicketType = view.findViewById(R.id.rvTicketTypes)
            ivUpArrow = view.findViewById(R.id.ivUpArrow)
            tvMveName = view.findViewById(R.id.tvMveName)
            tvCinemaName = view.findViewById(R.id.tvCinemaName)
            ivExperience = view.findViewById(R.id.ivExperience)
            showtime = view.findViewById(R.id.tvTime)
            tvTotalAmount = view.findViewById(R.id.tvTotalAmount)
            tvDate = view.findViewById(R.id.tvDate)
            tvSelectedSeats = view.findViewById(R.id.tvSelectedSeats)
            ivBottomNotch = view.findViewById(R.id.ivBottomNotch)
            tvExperienceName = view.findViewById(R.id.tvExperiencename)
            rrTicketDetailsLayout = view.findViewById(R.id.rrTicketDetailsLayout)
            constraintLayout = view.findViewById(R.id.constraintLayout)
            constraintLayout.setOnClickListener {
                rrTicketDetailsLayout.visibility = View.VISIBLE
                Picasso.with(mcontext).load(R.drawable.uparrow_bookinghistory).into(ivBottomNotch)
            }
            ivUpArrow.setOnClickListener {
                rrTicketDetailsLayout.visibility = View.GONE
                Picasso.with(mcontext).load(R.drawable.down_bookinghistory).into(ivBottomNotch)
            }
            /*  tvAgeGroup = view.findViewById(R.id.tvAgeGroup)
              tvTicketCount = view.findViewById(R.id.ticketCount)
              tvAmount = view.findViewById(R.id.txtAmount)*/

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_lastseen_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (position == 0) {
            holder.rrTicketDetailsLayout.visibility = View.VISIBLE
            Picasso.with(mcontext).load(R.drawable.uparrow_bookinghistory).into(holder.ivBottomNotch)
        } else {
            Picasso.with(mcontext).load(R.drawable.down_bookinghistory).into(holder.ivBottomNotch)
        }
        val eachItem = lastSeenData.get(position)
        holder.tvMveName.setText(eachItem.moviename)
        holder.tvCinemaName.setText(eachItem.cinemaname)
        holder.tvDate.setText(eachItem.showdate)
        holder.showtime.setText(eachItem.showtime)
        holder.tvTotalAmount.setText("$" + eachItem.ticket_total_amount)
        val Seats = lastSeenData.get(position).seatinfo.replace(",", ", ");
        holder.tvSelectedSeats.setText(Seats)
        if (eachItem.experience_img.isNotBlank()) {
            if (eachItem.exprience_single_logo.equals("0")) {
                holder.ivExperience.getLayoutParams().width = mcontext!!.resources.getDimension(R.dimen.imageview_width).toInt()
                holder.ivExperience.requestLayout()
                holder.ivExperience.visibility = View.VISIBLE
                holder.tvExperienceName.visibility = View.GONE
                Picasso.with(mcontext).load(eachItem.experience_img).into(holder.ivExperience)
            } else {
                Picasso.with(mcontext).load(eachItem.experience_img).into(holder.ivExperience)
                holder.ivExperience.visibility = View.VISIBLE
                holder.tvExperienceName.visibility = View.GONE}
        } else {
            holder.tvExperienceName.setText(eachItem.experience_name)
            holder.tvExperienceName.visibility = View.VISIBLE
            holder.ivExperience.visibility = View.GONE
        }
        holder.constraintLayout.background = mcontext!!.getDrawable(R.drawable.lastseen_bg_icon)
        // Picasso.with(mcontext).load(R.drawable.lastseen_bg_icon).into(holder.constraintLayout)
        holder.rvTicketType.setHasFixedSize(true)
        holder.rvTicketType.setNestedScrollingEnabled(false);
        holder.rvTicketType.setLayoutManager(LinearLayoutManager(mcontext, LinearLayoutManager.VERTICAL, false))
        val mAdapter = TicketTypeAdapter(lastSeenData.get(position).ticketdescription, mcontext)
        holder.rvTicketType.adapter = mAdapter
        holder.ivBottomNotch.setOnClickListener {
            if (holder.rrTicketDetailsLayout.visibility == View.VISIBLE) {
                holder.rrTicketDetailsLayout.visibility = View.GONE
                Picasso.with(mcontext).load(R.drawable.down_bookinghistory).into(holder.ivBottomNotch)
            } else {
                holder.rrTicketDetailsLayout.visibility = View.VISIBLE
                Picasso.with(mcontext).load(R.drawable.uparrow_bookinghistory).into(holder.ivBottomNotch)
            }
        }
        /*  try {

              holder.tvTicketCount.text = Item.receipt.items.get(position).single_price + " X " +
                      Item.receipt.items.get(position).quantity
              holder.tvAmount.text = Item.receipt.items.get(position).price
              holder.tvAgeGroup.text = Item.receipt.items.get(position).name
          } catch (e: Exception) {
              e.printStackTrace()
          }
  */
    }

    override fun getItemCount(): Int {
        return lastSeenData.size
    }
}
