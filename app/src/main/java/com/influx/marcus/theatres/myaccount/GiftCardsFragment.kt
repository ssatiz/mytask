package com.influx.marcus.theatres.myaccount

import android.app.Activity.RESULT_OK
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.enrollgiftcard.EnrollGiftCardResp
import com.influx.marcus.theatres.api.ApiModels.getgiftcardbalance.GiftCardBalanceReq
import com.influx.marcus.theatres.api.ApiModels.getgiftcardbalance.GiftCardBalanceResp
import com.influx.marcus.theatres.api.ApiModels.giftdelete.GiftDeleteReq
import com.influx.marcus.theatres.api.ApiModels.giftdelete.GiftDeleteResp
import com.influx.marcus.theatres.api.ApiModels.myaccount.GiftCardInfo
import com.influx.marcus.theatres.api.ApiModels.myaccount.GiftCards
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.UtilsDialog
import com.squareup.picasso.Picasso
import org.jetbrains.anko.alert
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast


class GiftCardsFragment : Fragment() {

    lateinit var mcontext: Context
    lateinit var myAccountVM: MyAccountVM
    lateinit var mAdapter: GiftCardAdapter

    companion object {
        lateinit var giftCardList: GiftCards
        fun newInstance(giftCards: GiftCards) = GiftCardsFragment().apply {
            giftCardList = giftCards
        }
    }

    private lateinit var rvMovies: RecyclerView
    private lateinit var btAdd: Button
    private lateinit var ivGiftCard: ImageView
    private lateinit var tvMagical: TextView
    val selectedPositionMap = HashMap<String, Int>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_gift_card, container, false)
        mcontext = (this.activity as Context?)!!
        rvMovies = rootView.findViewById(R.id.cardRecyclerView)
        rvMovies.setNestedScrollingEnabled(false);
        ivGiftCard = rootView.findViewById(R.id.ivGiftCard)
        rvMovies.setNestedScrollingEnabled(false)
        btAdd = rootView.findViewById(R.id.btAdd)
        tvMagical = rootView.findViewById(R.id.tvMagical)
        rvMovies.setHasFixedSize(true)
        rvMovies.setLayoutManager(LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false))
        myAccountObserver()
        btAdd.setOnClickListener {
            val intent = Intent(mcontext,
                    EnrollGiftCard::class.java)
            startActivityForResult(intent, 1)
            activity!!.overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)

        }
        tvMagical.setText(giftCardList.title)
        Picasso.with(mcontext).load(giftCardList.banner).into(ivGiftCard)
        try {
            mAdapter = GiftCardAdapter(giftCardList, mcontext, giftCardListener)
            rvMovies.setAdapter(mAdapter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return rootView
    }

    private fun myAccountObserver() {
        myAccountVM = ViewModelProviders.of(this).get(MyAccountVM::class.java)
        myAccountVM.getBalanceGiftData().observe(this, balanceObj)
        myAccountVM.getGiftCardDeleteResp().observe(this, giftdelResp)
        myAccountVM.getApiErrorData().observe(this, errorObs)
    }

    private var giftCardListener = object : GiftCardAdapter.GiftcardAdapterListener {
        override fun getBalance(position: Int) {
            selectedPositionMap.put(giftCardList.card_info[position].card_no, position)
            val request = GiftCardBalanceReq(giftCardList.card_info[position].card_no,
                    giftCardList.card_info[position].pin,
                    AppConstants.APP_VERSION, AppConstants.APP_PLATFORM)
            if (UtilsDialog.isNetworkStatusAvialable(mcontext)) {
                myAccountVM.getBalanceGiftCardResponse(request)
            }
        }

        override fun deleteCard(position: Int) {
            val cardNumber = giftCardList.card_info.get(position).card_no
            val userId = AppConstants.getString(AppConstants.KEY_USERID, mcontext)
            val request = GiftDeleteReq(
                    AppConstants.APP_PLATFORM,
                    AppConstants.APP_VERSION,
                    cardNumber,
                    userId
            )
            if (UtilsDialog.isNetworkStatusAvialable(mcontext)) {
                UtilsDialog.showProgressDialog(mcontext, "")
                myAccountVM.deleteThisGiftCard(request)
            }
        }
    }

    private var balanceObj = object : Observer<GiftCardBalanceResp> {
        override fun onChanged(t: GiftCardBalanceResp?) {
            if (t!!.STATUS) {
                // loop through list and get desired card, then get position from that card no.
                // then add new object
                var selectPosition = 0//default
                var positionKey = ""//default
                for (eachCard in giftCardList.card_info) {
                    for (eachCardno in selectedPositionMap.keys) {
                        if (eachCard.card_no.equals(eachCardno)) {
                            positionKey = eachCardno
                            selectPosition = selectedPositionMap.get(eachCardno)!!
                        }
                    }
                }
                selectedPositionMap.remove(positionKey)

                val card = GiftCardInfo(t.DATA.balanceInString,
                        giftCardList.card_info[selectPosition].card_name,
                        giftCardList.card_info[selectPosition].card_no,
                        giftCardList.card_info[selectPosition].pin,
                        giftCardList.card_info[selectPosition].qrcode_url,
                        giftCardList.card_info[selectPosition].card_image) as GiftCardInfo
                giftCardList.card_info.removeAt(selectPosition)
                giftCardList.card_info.add(selectPosition, card)
                mAdapter = GiftCardAdapter(giftCardList, mcontext, giftCardListener)
                rvMovies.setAdapter(mAdapter)
            } else {
                alert(getString(R.string.ohinternalservererror),
                        getString(R.string.marcus_theatre_title)) {
                    positiveButton("OK") {
                        it.dismiss()
                    }
                }.show()
            }
        }
    }

    private var giftdelResp = object : Observer<GiftDeleteResp> {
        override fun onChanged(t: GiftDeleteResp?) {
            UtilsDialog.hideProgress()
            if (t!!.STATUS) {
                giftCardList.card_info.clear()
                for (eachCard in t!!.DATA.card_info) {
                    val cardInfo = GiftCardInfo(
                            "",
                            eachCard.card_name,
                            eachCard.card_no,
                            eachCard.pin,
                            eachCard.qrcode_url,
                            eachCard.card_image
                    )
                    mAdapter = GiftCardAdapter(giftCardList, mcontext, giftCardListener)
                    giftCardList.card_info.add(cardInfo)
                }
                rvMovies.setAdapter(mAdapter)
            } else {
                alert(getString(R.string.ohinternalservererror),
                        getString(R.string.marcus_theatre_title)) {
                    positiveButton("OK") {
                        it.dismiss()
                    }
                }.show()
            }
        }
    }

    private var errorObs = object : Observer<String> {
        override fun onChanged(t: String?) {
            Handler().postDelayed({
                UtilsDialog.hideProgress()
            }, 3000)
            alert(getString(R.string.ohinternalservererror),
                    getString(R.string.marcus_theatre_title)) {
                positiveButton("OK") {
                    it.dismiss()
                }
            }.show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == 1 && resultCode == RESULT_OK) {
                val gift_card_obj: EnrollGiftCardResp = AppConstants.getObject(AppConstants.KEY_GIFT_CARD,
                        mcontext, EnrollGiftCardResp::class.java as Class<Any>) as EnrollGiftCardResp
                val pin = data!!.getStringExtra("giftPinNo");
                val name = giftCardList.card_info.size + 1;
                val card = GiftCardInfo("", "Card " + name, gift_card_obj.DATA.secureAccountNumber, pin, gift_card_obj.DATA.qrcode_url, gift_card_obj.DATA.card_image) as GiftCardInfo
                giftCardList.card_info.add(card)
                mAdapter.notifyDataSetChanged()
            }
        } catch (ex: Exception) {
           // toast(ex.toString())
        }
    }
}
