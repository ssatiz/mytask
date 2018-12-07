package com.influx.marcus.theatres.payment

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.paymentcardlist.SavedCard
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.LogUtils
import com.squareup.picasso.Picasso
import org.jetbrains.anko.alert


/**
 * Created by influx on 02-04-2018.
 */
class SavedCardAdapter(dataList: List<SavedCard>, cardAdapterlistener: cardAdapterListener, val mcontext: Context?) : RecyclerView.Adapter<SavedCardAdapter.MyViewHolder>() {
    var dataType: String = ""
    var SavedCardItem = dataList
    var cardAdapterlistener = cardAdapterlistener
    var isSave = false
    var cvv = ""
    var zipCode = ""
    var previousIndex = 0
    var currentIndex = 0


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        lateinit var ivCard: ImageView
        lateinit var tvName: TextView
        lateinit var tvCardNo: TextView
        lateinit var tvCvv: TextView
        lateinit var tvZipcode: TextView
        lateinit var clDetail: LinearLayout
        lateinit var rbChoose: RadioButton
        lateinit var inputCVV: android.support.design.widget.TextInputLayout
        lateinit var inputZip: android.support.design.widget.TextInputLayout
        lateinit var ivDustbin: ImageView


        init {
            tvCardNo = view.findViewById(R.id.tvCardNo)
            ivCard = view.findViewById(R.id.ivCard)
            tvName = view.findViewById(R.id.tvName)
            tvCvv = view.findViewById(R.id.tvCVV)
            tvZipcode = view.findViewById(R.id.tvZipcode)
            clDetail = view.findViewById(R.id.clDetail)
            rbChoose = view.findViewById(R.id.rbChoose)
            inputCVV = view.findViewById(R.id.inputCVV)
            inputZip = view.findViewById(R.id.inputZip)
            ivDustbin = view.findViewById(R.id.ivDustbin)
        }
    }

    interface cardAdapterListener {
        fun savecard(save: Boolean, position: Int)
        fun savecarddetail(position: Int)
        fun deleteCard(position: Int)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_savedcard_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        try {
            holder.tvZipcode.setText("")
            holder.tvCvv.setText("")

            val item = SavedCardItem.get(position)

            if (item.isSelect == true && item.isVisible == false) {
                holder.clDetail.visibility = View.VISIBLE
                holder.rbChoose.isChecked = true
            } else {
                holder.clDetail.visibility = View.GONE
                holder.rbChoose.isChecked = false
            }
            holder.inputCVV.isErrorEnabled = false
            holder.inputZip.isErrorEnabled = false
            holder.tvCardNo.text = item.masked_no
            holder.tvName.text = item.first_name + " " + SavedCardItem.get(position).last_name

            // CardView c  = (CardView)rvSellRecords.getItem(int position);
            if (!item.card_image.isNullOrBlank()) {
                Picasso.with(mcontext).load(item.card_image).into(holder.ivCard)
            }
            holder.ivCard.setOnClickListener {
                updateUiForSavedCard(holder, position, item)
            }
            holder.tvCardNo.setOnClickListener {
                updateUiForSavedCard(holder, position, item)
            }
            holder.tvName.setOnClickListener {
                updateUiForSavedCard(holder, position, item)
            }
            holder.rbChoose.setOnClickListener {
                updateUiForSavedCard(holder, position, item)
            }

            holder.tvZipcode.setOnClickListener {
                if (cvv.length < 1) {
                    holder.inputCVV.isErrorEnabled = true
                    holder.inputCVV.error = "Please enter CVV"
                }
            }

            holder.tvZipcode.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    zipCode = s.toString()
                    if (zipCode.length > 4) {
                        AppConstants.putString(AppConstants.KEY_ZIPCODE, zipCode, mcontext!!)
                        cardAdapterlistener.savecarddetail(position)
                    } else {
                        /*holder.inputZip.isErrorEnabled = true
                        holder.inputZip.error = "Please enter ZipCode"*/
                    }
                }

                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, aft: Int) {
                }

                override fun afterTextChanged(s: Editable) {
                    //call your function here of calculation here
                }
            })

            holder.tvCvv.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.toString().length > 1) {
                        cvv = s.toString()
                        AppConstants.putString(AppConstants.KEY_CVV, cvv, mcontext!!)
                    } else {
                        LogUtils.d("PAYMENT", "CVV is 0 lenght")
                    }

                }

                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, aft: Int) {

                }

                override fun afterTextChanged(s: Editable) {
                    //call your function here of calculation here
                }
            })
            holder.ivDustbin.setOnClickListener {
                mcontext!!.alert(mcontext.getString(R.string.remove_saved_card_warning), "Marcus Theatres")
                {
                    positiveButton("Yes") { dialog ->
                        cardAdapterlistener.deleteCard(position)
                    }
                    negativeButton("No") {
                        it.dismiss()
                    }
                }.show().setCancelable(false)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateUiForSavedCard(holder: MyViewHolder, position: Int, item: SavedCard) {
        cardAdapterlistener.savecard(item.isVisible, position)
    }

    override fun getItemCount(): Int {
        return SavedCardItem.size
    }

}
