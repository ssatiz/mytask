package com.influx.marcus.theatres.myaccount

import android.app.Activity.RESULT_OK
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.constraint.ConstraintLayout
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
import com.influx.marcus.theatres.api.ApiModels.DeleteCard.DeleteCardReq
import com.influx.marcus.theatres.api.ApiModels.DeleteCard.DeleteCardResp
import com.influx.marcus.theatres.api.ApiModels.myaccount.CardInfo
import com.influx.marcus.theatres.api.ApiModels.myaccount.LoyaltyNo
import com.influx.marcus.theatres.api.ApiModels.rewardscardslist.CARDLISTDATA
import com.influx.marcus.theatres.api.ApiModels.rewardscardslist.RewardsCardsListResp
import com.influx.marcus.theatres.signup.MagicalSignup
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.UtilsDialog
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast


class Rewards : Fragment() {

    lateinit var mcontext: Context
    lateinit var myAccountVM: MyAccountVM

    companion object {
        lateinit var loyaltyNo: LoyaltyNo
        lateinit var cardlistdata: CARDLISTDATA
        fun newInstance(loyalty: LoyaltyNo) = Rewards().apply {
            loyaltyNo = loyalty
        }
    }

    private lateinit var rvMovies: RecyclerView
    private lateinit var btAdd: Button
    private lateinit var clReward: ImageView
    private lateinit var tvMagical: TextView
    private lateinit var tvJoinNow: TextView
    private lateinit var cl_enroll_layout: ConstraintLayout
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.rewards, container, false)
        mcontext = (this.activity as Context?)!!
        rvMovies = rootView.findViewById(R.id.cardRecyclerView)
        clReward = rootView.findViewById(R.id.ivReward)
        rvMovies.setNestedScrollingEnabled(false)
        btAdd = rootView.findViewById(R.id.btAdd)
        tvMagical = rootView.findViewById(R.id.tvMagical)
        tvJoinNow = rootView.findViewById(R.id.tvJoinnow)
        cl_enroll_layout = rootView.findViewById(R.id.cl_enroll_layout)

        rvMovies.setHasFixedSize(true)
        rvMovies.setLayoutManager(LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false))
        myAccountObserver()
        btAdd.setOnClickListener {
            val intent = Intent(mcontext,
                    EnrollCard::class.java)
            startActivityForResult(intent, 1)
        }
        tvJoinNow.setOnClickListener {
            AppConstants.putString(AppConstants.KEY_ISMMR, "mmr", context!!)
            val i = Intent(context, MagicalSignup::class.java)
            i.putExtra("sidemenu", "false")
            startActivity(i)
            activity!!.overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)

        }

        tvMagical.setText(loyaltyNo.title)
        /*Picasso.with(mcontext).load(loyaltyNo.banner).into(clReward, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {
                        llLoader.visibility = View.GONE
                        clReward.visibility = View.VISIBLE }

                    override fun onError() {
                        llLoader.visibility = View.GONE }
                })*/

        if (loyaltyNo.card_info.size > 0) {
            cl_enroll_layout.visibility = View.GONE
            try {
                val mAdapter = CardAdapter(loyaltyNo, delcardlistener, mcontext)
                rvMovies.setAdapter(mAdapter)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            cl_enroll_layout.visibility = View.VISIBLE
        }
        return rootView
    }

    private var delcardlistener = object : CardAdapter.cardAdapterListener {

        override fun delete(v: View, position: Int, cardno: String) {

            alert("Are you sure you want to delete this card?", "Marcus Theatres")
            {
                positiveButton("YES") { dialog ->
                    val request = DeleteCardReq(AppConstants.getString(AppConstants.KEY_USERID,
                            mcontext), cardno, AppConstants.APP_VERSION, AppConstants.APP_PLATFORM)
                    if (UtilsDialog.isNetworkStatusAvialable(mcontext)) {
                        UtilsDialog.showProgressDialog(mcontext, "")
                        myAccountVM.getDeleteDetailResponse(request)
                    }
                    dialog.dismiss()
                }
                negativeButton("NO") { dialog ->
                    dialog.dismiss()
                }
            }.show().setCancelable(false)
        }
    }

    private fun myAccountObserver() {
        myAccountVM = ViewModelProviders.of(this).get(MyAccountVM::class.java)
        myAccountVM.getDeleteDetailData().observe(this, deleteDetailsObs)
        myAccountVM.getApiErrorData().observe(this, errorObs)
    }

    private var errorObs = object : Observer<String> {
        override fun onChanged(t: String?) {
            Handler().postDelayed({
                UtilsDialog.hideProgress()
            }, 3000)
        }
    }
    private var CardListObs = object : Observer<RewardsCardsListResp> {
        override fun onChanged(t: RewardsCardsListResp?) {
            UtilsDialog.hideProgress()
            if (t!!.STATUS) {

            } else {

            }

        }
    }
    private var deleteDetailsObs = object : Observer<DeleteCardResp> {
        override fun onChanged(t: DeleteCardResp?) {
            UtilsDialog.hideProgress()

            if (t!!.DATA.card_info != null) {
                if (t!!.DATA.card_info.size > 0) {
                    var cardList: ArrayList<CardInfo> = ArrayList()
                    lateinit var loylatyNumber: LoyaltyNo
                    t!!.DATA.card_info.forEachIndexed { index, eachCard ->
                        val individualCard = CardInfo(
                                eachCard.card_name,
                                eachCard.card_no,
                                eachCard.card_holder_name,
                                eachCard.card_image,
                                eachCard.qrcode_url
                        )
                        cardList.add(individualCard)
                    }
                    System.out.println("cardlistsize" + cardList.size)
                    loylatyNumber = LoyaltyNo(t.DATA.banner, t.DATA.title, cardList)
                    val mAdapter = CardAdapter(loylatyNumber, delcardlistener, mcontext)
                    rvMovies.setAdapter(mAdapter)
                    mAdapter.notifyDataSetChanged()
                    showAlert(loylatyNumber.card_info.size)
                }
            } else if (t!!.DATA.card_info == null) {
                AppConstants.putString(AppConstants.KEY_LOYALTYCARDNO, "", mcontext)
                var cardList: ArrayList<CardInfo> = ArrayList()
                val loylatyNumber1 = LoyaltyNo("", "", cardList)
                val mAdapter = CardAdapter(loylatyNumber1, delcardlistener, mcontext)
                rvMovies.setAdapter(mAdapter)
                mAdapter.notifyDataSetChanged()
                showAlert(loylatyNumber1.card_info.size)
            }

        }
    }

    private fun showAlert(size: Int) {
        alert("Card has been deleted successfully", "Marcus Theatres")
        {
            positiveButton("OK") { dialog ->
                dialog.dismiss()
                if (size > 0) {
                    cl_enroll_layout.visibility = View.GONE
                } else {
                    cl_enroll_layout.visibility = View.VISIBLE
                }
            }
        }.show().setCancelable(false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            super.onActivityResult(requestCode, resultCode, data)

            if (requestCode == 1 && resultCode == RESULT_OK) {
                val loyalty: LoyaltyNo = AppConstants.getObject(AppConstants.KEY_CARDLIST,
                        mcontext, LoyaltyNo::class.java as Class<Any>) as LoyaltyNo

                val mAdapter = CardAdapter(loyalty, delcardlistener, mcontext)
                rvMovies.setAdapter(mAdapter)
                mAdapter.notifyDataSetChanged()
                if (loyalty.card_info.size > 0) {
                    cl_enroll_layout.visibility = View.GONE
                } else {
                    cl_enroll_layout.visibility = View.VISIBLE
                }
            }
        } catch (ex: Exception) {
        }

    }

    override fun onAttachFragment(childFragment: Fragment?) {
        super.onAttachFragment(childFragment)
    }
}
