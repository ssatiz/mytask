package com.influx.marcus.theatres.myaccount

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Html
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.enrollcard.EnrollCardReq
import com.influx.marcus.theatres.api.ApiModels.enrollcard.EnrollCardResp
import com.influx.marcus.theatres.api.ApiModels.myaccount.CardInfo
import com.influx.marcus.theatres.api.ApiModels.myaccount.LoyaltyNo
import com.influx.marcus.theatres.api.ApiModels.rewardscardslist.RewardCardsListReq
import com.influx.marcus.theatres.api.ApiModels.rewardscardslist.RewardsCardsListResp
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.UtilsDialog
import kotlinx.android.synthetic.main.activity_enroll.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.toast


class EnrollCard : AppCompatActivity() {

    lateinit var myAccountVM: MyAccountVM
    private var context: Context = this@EnrollCard
    private var message: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enroll)
        myAccountObserver()
        initViews()
        btAddCard.setOnClickListener {
            validate()
        }
        ivBack.setOnClickListener {

            onBackPressed()
        }
    }

    private fun initViews() {
//        if (AppConstants.getString(AppConstants.KEY_EMAIL, context).isNotBlank()) {
//            etEmailid.setText(AppConstants.getString(AppConstants.KEY_EMAIL, context))
//        }
    }


    private fun myAccountObserver() {
        myAccountVM = ViewModelProviders.of(this).get(MyAccountVM::class.java)
        myAccountVM.getEnrollData().observe(this, enrollCardObs)
        myAccountVM.getcardListData().observe(this, CardListObs)
        myAccountVM.getApiErrorData().observe(this, errorObs)
    }

    private var errorObs = object : Observer<String> {
        override fun onChanged(t: String?) {
            UtilsDialog.hideProgress()
            alert( getString(R.string.ohinternalservererror),
                    getString(R.string.marcus_theatre_title)) {
                positiveButton("OK") {
                    it.dismiss()
                }
            }.show()

        }
    }

    private var enrollCardObs = object : Observer<EnrollCardResp> {
        override fun onChanged(t: EnrollCardResp?) {
            UtilsDialog.hideProgress()
            if (t!!.STATUS) {
                message = t.DATA.success_msg
                val request = RewardCardsListReq(AppConstants.getString(AppConstants.KEY_USERID,
                        this@EnrollCard), AppConstants.APP_VERSION, AppConstants.APP_PLATFORM)
                if (UtilsDialog.isNetworkStatusAvialable(this@EnrollCard)) {
                    UtilsDialog.showProgressDialog(this@EnrollCard, "")
                    myAccountVM.getCardListResponse(request)
                }
            } else {
                alert( t.DATA.message,
                        getString(R.string.marcus_theatre_title)) {
                    positiveButton("OK") {
                        it.dismiss()
                    }
                }.show()
            }

        }
    }

    private var CardListObs = object : Observer<RewardsCardsListResp> {
        override fun onChanged(t: RewardsCardsListResp?) {
            if (t!!.STATUS) {
                val cardList: ArrayList<CardInfo> = ArrayList()
                lateinit var loylatyNumber: LoyaltyNo
                t.DATA.card_info.forEachIndexed { index, eachCard ->
                    val individualCard = CardInfo(
                            eachCard.card_name,
                            eachCard.card_no,
                            eachCard.card_holder_name,
                            eachCard.card_image,
                            eachCard.qrcode_url)
                    cardList.add(individualCard)
                }
                System.out.println("cardlistsize" + cardList.size)
                loylatyNumber = LoyaltyNo(t.DATA.banner, t.DATA.title, cardList)
                AppConstants.putObject(AppConstants.KEY_CARDLIST, loylatyNumber, this@EnrollCard)
                AppConstants.putString(AppConstants.KEY_LOYALTYCARDNO, loylatyNumber.card_info.get(0).card_no, this@EnrollCard)
                message = message.replace("\\n", "<br>")
                alert(Html.fromHtml(message), "")
                {
                    positiveButton("OK") { dialog ->
                        UtilsDialog.hideProgress()
                        dialog.dismiss()

                        val intent = intent
                        intent.putExtra("Key", "success")
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                        overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
                    }

                }.show()

            }else{
                alert(getString(R.string.ohinternalservererror),
                        getString(R.string.marcus_theatre_title)) {
                    positiveButton("OK") {
                        it.dismiss()
                    }
                }.show()
            }

        }
    }

    fun validate() {
        inputCardNo.isErrorEnabled = false
        inputEmail.isErrorEnabled = false
        if (etCardNo.text.toString().isBlank()) {
            inputCardNo.isErrorEnabled = true
            inputCardNo.error = "Please enter Card Number"
        } else if (etEmailid.text.toString().isBlank()) {
            inputEmail.isErrorEnabled = true
            inputEmail.error = "Please enter Email/Last Name"
        } else {
            val request = EnrollCardReq(AppConstants.getString(AppConstants.KEY_USERID,
                    this@EnrollCard), etCardNo.text.toString(), etEmailid.text.toString())
            if (UtilsDialog.isNetworkStatusAvialable(this@EnrollCard)) {
                UtilsDialog.showProgressDialog(this@EnrollCard, "")
                myAccountVM.getEnrollCardResponse(request)
            }
        }
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.animator.slide_from_left , R.animator.slide_to_right)
    }
}
