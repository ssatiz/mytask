package com.influx.marcus.theatres.myaccount

import android.app.Dialog
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
import com.influx.marcus.theatres.api.ApiModels.myaccount.ComingUp
import com.squareup.picasso.Picasso
import org.jetbrains.anko.matchParent


/**
 * Created by influx on 02-04-2018.
 */
class ComingUpAdapter(dataList: List<ComingUp>, context: Context?) : RecyclerView.Adapter<ComingUpAdapter.MyViewHolder>() {
    // var Item = dataList
    var comingupData = dataList
    var size = 0
    var mcontext = context

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // var tvAgeGroup: TextView
        lateinit var rvTicketType: RecyclerView
        lateinit var genre: TextView
        lateinit var tvAmount: TextView
        lateinit var ivUpArrow: ImageView
        lateinit var tvCinemaName: TextView
        lateinit var tvMoviename: TextView
        lateinit var tvConfirmationNo: TextView
        lateinit var ivCinema: ImageView
        lateinit var tvDate: TextView
        lateinit var tvTime: TextView
        lateinit var ivImg: ImageView
        lateinit var tvSelectedSeats: TextView
        lateinit var tvDaystogo: TextView
        lateinit var tvTotalAmount: TextView
        lateinit var rrTicketDetailsLayout: RelativeLayout
        lateinit var rlv_loading: RelativeLayout
        lateinit var constraintLayout: ConstraintLayout
        lateinit var ivQr: ImageView
        lateinit var ivExpand: ImageView
        lateinit var tvExpand: TextView
        lateinit var tvExperience: TextView
        lateinit var loader: com.wang.avi.AVLoadingIndicatorView

        init {
            rvTicketType = view.findViewById(R.id.rvTicketTypes)
            ivUpArrow = view.findViewById(R.id.ivUpArrow)
            tvCinemaName = view.findViewById(R.id.tvCinemaName)
            tvMoviename = view.findViewById(R.id.tvMovieName)
            tvConfirmationNo = view.findViewById(R.id.tvConfirmationNo)
            ivCinema = view.findViewById(R.id.ivCinema)
            tvDate = view.findViewById(R.id.tvDate)
            tvTime = view.findViewById(R.id.tvTime)
            ivImg = view.findViewById(R.id.ivImg)
            //   loader = view.findViewById(R.id.loader)

            tvTotalAmount = view.findViewById(R.id.tvTotalAmount)
            tvSelectedSeats = view.findViewById(R.id.tvSelectedSeats)
            tvDaystogo = view.findViewById(R.id.tvDaystogo)
            ivQr = view.findViewById(R.id.ivQr)
            rrTicketDetailsLayout = view.findViewById(R.id.rrTicketDetailsLayout)
            rlv_loading = view.findViewById(R.id.rlv_loading)
            constraintLayout = view.findViewById(R.id.constraintLayout)
            ivExpand = view.findViewById(R.id.ivExpand)
            tvExpand = view.findViewById(R.id.tvScan)
            tvExperience = view.findViewById(R.id.tvExperienceName)
            constraintLayout.setOnClickListener {
                rrTicketDetailsLayout.visibility = View.VISIBLE
            }
            ivUpArrow.setOnClickListener {
                rrTicketDetailsLayout.visibility = View.GONE
            }
            /* tvAgeGroup = view.findViewById(R.id.tvAgeGroup)
             tvAmount = view.findViewById(R.id.txtAmount)*/

        }
    }

    private fun openQrPopup(imgUrl: String) {
        val dialog = Dialog(mcontext)
        dialog.setContentView(R.layout.popup_qrcodelayout);
        dialog.getWindow().setLayout(matchParent, matchParent)
        val ivQR: ImageView = dialog!!.findViewById(R.id.ivQrimg)
        val ivClose: ImageView = dialog!!.findViewById(R.id.ivClose)
        val loader: com.wang.avi.AVLoadingIndicatorView = dialog!!.findViewById(R.id.loader)

        Picasso.with(mcontext).load(imgUrl).into(ivQR, object : com.squareup.picasso.Callback {
            override fun onSuccess() {
                loader.visibility = View.GONE
                ivQR.visibility = View.VISIBLE
            }

            override fun onError() {
                loader.visibility = View.GONE
            }
        })
        ivClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show();
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_cominguplayout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (position == 0) {
            holder.rrTicketDetailsLayout.visibility = View.VISIBLE
        }
        val dataItem = comingupData.get(position)

        holder.tvCinemaName.setText(dataItem.cinemaname + " - " + dataItem.screenname)
        holder.tvMoviename.setText(comingupData.get(position).moviename)
        holder.tvConfirmationNo.setText("Confirmation Number:" + dataItem.booking_id)
        holder.tvDate.setText(dataItem.showdate)
        holder.tvTime.setText(dataItem.showtime)
        if (dataItem.experience_img.isNotEmpty()) {
            if (dataItem.exprience_single_logo.equals("0")) {
                holder.ivCinema.getLayoutParams().width = mcontext!!.resources.getDimension(R.dimen.imageview_width).toInt()
                holder.ivCinema.requestLayout()
                holder.ivCinema.visibility = View.VISIBLE
                holder.tvExperience.visibility = View.GONE
                Picasso.with(mcontext).load(dataItem.experience_img).into(holder.ivCinema)
            } else {
                Picasso.with(mcontext).load(dataItem.experience_img).into(holder.ivCinema)
                holder.ivCinema.visibility = View.VISIBLE
                holder.tvExperience.visibility = View.GONE
            }
        } else {
            holder.ivCinema.visibility = View.GONE
            holder.tvExperience.visibility = View.VISIBLE
            holder.tvExperience.setText(dataItem.experience_name)
        }

        if (comingupData.get(position).movieimage.isNotEmpty()) {
            Picasso.with(mcontext).load(comingupData.get(position).movieimage).into(holder.ivImg, object : com.squareup.picasso.Callback {
                override fun onSuccess() {
                    //  holder.loader.visibility = View.GONE
                    holder.ivImg.visibility = View.VISIBLE
                }

                override fun onError() {
                    //  holder.loader.visibility = View.GONE
                }
            })
        } else {
            holder.ivImg.visibility = View.VISIBLE

        }
        if (comingupData.get(position).qrcode_url.isNotBlank()) {
            holder.rlv_loading.visibility = View.VISIBLE
            Picasso.with(mcontext)
                    .load(comingupData.get(position).qrcode_url)
                    .into(holder.ivQr, object : com.squareup.picasso.Callback {
                        override fun onError() {
                            holder.rlv_loading.visibility = View.GONE
                        }

                        override fun onSuccess() {
                            holder.rlv_loading.visibility = View.GONE
                        }
                    })
        }
        holder.tvExpand.setOnClickListener {
            openQrPopup(comingupData.get(position).qrcode_url)
        }
        holder.ivExpand.setOnClickListener {
            openQrPopup(comingupData.get(position).qrcode_url)
        }
        holder.ivQr.setOnClickListener {
            openQrPopup(comingupData.get(position).qrcode_url)
        }

        val Seats = comingupData.get(position).seatinfo.replace(",", ", ");
        holder.tvSelectedSeats.setText(Seats)
        holder.tvTotalAmount.setText("$" + comingupData.get(position).ticket_total_amount)
        holder.tvDaystogo.setText(comingupData.get(position).daystogo)
        holder.rvTicketType.setHasFixedSize(true)
        holder.rvTicketType.setNestedScrollingEnabled(false);
        holder.rvTicketType.setLayoutManager(LinearLayoutManager(mcontext, LinearLayoutManager.VERTICAL, false))
        val mAdapter = TicketTypeAdapter(comingupData.get(position).ticketdescription, mcontext)
        holder.rvTicketType.adapter = mAdapter

    }

    override fun getItemCount(): Int {
        return comingupData.size
    }
}
