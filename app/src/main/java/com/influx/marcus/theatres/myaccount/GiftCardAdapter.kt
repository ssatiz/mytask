package com.influx.marcus.theatres.myaccount

import android.app.Dialog
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.hanks.htextview.typer.TyperTextView
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.myaccount.GiftCards
import com.squareup.picasso.Picasso
import com.wang.avi.AVLoadingIndicatorView
import org.jetbrains.anko.alert
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.toast


/**
 * Created by influx on 02-04-2018.
 */
class GiftCardAdapter(dataList: GiftCards, context: Context?, GiftcardAdapterListener: GiftcardAdapterListener) : RecyclerView.Adapter<GiftCardAdapter.MyViewHolder>() {
    var GiftCards = dataList
    var mcontext = context
    var giftCardListener = GiftcardAdapterListener

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvCardCount: TextView = view.findViewById(R.id.tvCardCount)
        var ivCard: ImageView = view.findViewById(R.id.ivCard)
        var tvName: TextView = view.findViewById(R.id.tvName)
        var tvCardNo: TextView = view.findViewById(R.id.tvCardNo)
        var tvGetBalance: TextView = view.findViewById(R.id.tvGetBalance)
        var tvFetchingBalance: TyperTextView = view.findViewById(R.id.tvFetchingBalance)
        var tvDelete: TextView = view.findViewById(R.id.tvDelete)
        var qrCode: ImageView = view.findViewById(R.id.ivQr)
    }

    interface GiftcardAdapterListener {
        fun getBalance(position: Int)
        fun deleteCard(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.inflate_gift_cards, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        try {
            val eachItem = GiftCards.card_info.get(position)
            holder.tvCardCount.text = eachItem.card_name
            holder.tvCardNo.text = eachItem.card_no
            Picasso.with(mcontext).load(eachItem.card_image).into(holder.ivCard)
            holder.ivCard.tag = eachItem.card_image
            if (eachItem.balance != null && eachItem.balance != "") {
                holder.tvGetBalance.visibility = View.VISIBLE
                holder.tvFetchingBalance.visibility = View.GONE
                holder.tvGetBalance.text = "Card Balance: " + eachItem.balance
                holder.tvGetBalance.tag = 1
            } else {
                holder.tvGetBalance.visibility = View.GONE
                holder.tvFetchingBalance.visibility = View.VISIBLE
                holder.tvGetBalance.text = "Get Balance"
                holder.tvGetBalance.tag = 2
            }
            if (holder.tvGetBalance.tag == 2) {
                giftCardListener.getBalance(position)
            }

//            holder.tvGetBalance.setOnClickListener {
//                if (holder.tvGetBalance.tag == 2) {
//                    giftCardListener.getBalance(position)
//                }
//            }


            holder.tvDelete.setOnClickListener {
                mcontext!!.alert("Are you sure you want to remove this Gift Card", "Marcus Theatres")
                {
                    positiveButton("Yes") { dialog ->
                        giftCardListener.deleteCard(position)
                    }
                    negativeButton("No") {
                        it.dismiss()
                    }
                }.show().setCancelable(false)
            }
            holder.qrCode.setOnClickListener {
                openQRPopup(eachItem.qrcode_url, eachItem.pin)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return GiftCards.card_info.size
    }

    private fun openQRPopup(imgUrl: String, pin: String) {
        val dialog = Dialog(mcontext)
        dialog.setContentView(R.layout.popup_qrcodelayout);
        dialog.getWindow().setLayout(matchParent, matchParent)
        val avLoadingIndicatorView: AVLoadingIndicatorView = dialog!!.findViewById(R.id.loader)
        val ivQR: ImageView = dialog!!.findViewById(R.id.ivQrimg)
        val ivClose: ImageView = dialog!!.findViewById(R.id.ivClose)
        val tvpin: TextView = dialog!!.findViewById(R.id.tvPinNo)
        if (imgUrl.isNotBlank()) {
            Picasso.with(mcontext)
                    .load(imgUrl)
                    .into(ivQR, object : com.squareup.picasso.Callback {
                        override fun onSuccess() {
                            avLoadingIndicatorView.visibility = View.GONE
                            ivQR.visibility = View.VISIBLE
                        }

                        override fun onError() {
                            avLoadingIndicatorView.visibility = View.GONE
                        }
                    })
        }
        if (pin.isNotEmpty()) {
            tvpin.visibility = View.VISIBLE
            tvpin.text = "Pin - " + pin
        }
        ivClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show();
    }


}
