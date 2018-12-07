package com.influx.marcus.theatres.payment

import android.annotation.SuppressLint
import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.*
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.*
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.DeleteSavedCard.DeleteSavedCardReq
import com.influx.marcus.theatres.api.ApiModels.DeleteSavedCard.DeleteSavedCardResp
import com.influx.marcus.theatres.api.ApiModels.blockseat.BlockSeatResp
import com.influx.marcus.theatres.api.ApiModels.blockseat.DATA
import com.influx.marcus.theatres.api.ApiModels.bookingconfirmation.BookingConfirmationReq
import com.influx.marcus.theatres.api.ApiModels.bookingconfirmation.BookingConfirmationResp
import com.influx.marcus.theatres.api.ApiModels.cancelbooking.CancelBookReq
import com.influx.marcus.theatres.api.ApiModels.cancelbooking.CancelBookingResp
import com.influx.marcus.theatres.api.ApiModels.carddetail.CardDetailReq
import com.influx.marcus.theatres.api.ApiModels.carddetail.CardDetailResp
import com.influx.marcus.theatres.api.ApiModels.getgiftcardbalance.GiftCardBalanceReq
import com.influx.marcus.theatres.api.ApiModels.getgiftcardbalance.GiftCardBalanceResp
import com.influx.marcus.theatres.api.ApiModels.paymentcardlist.*
import com.influx.marcus.theatres.api.ApiModels.showtime.ShowtimeRequest
import com.influx.marcus.theatres.api.ApiModels.showtime.ShowtimeResponse
import com.influx.marcus.theatres.api.RestClient
import com.influx.marcus.theatres.bookingconfirmation.BookingConfirmation
import com.influx.marcus.theatres.bookingconfirmation.ConfirmationVM
import com.influx.marcus.theatres.common.MonthYearPicker
import com.influx.marcus.theatres.common.PaymentConstants
import com.influx.marcus.theatres.homepage.PrivacyPolicy
import com.influx.marcus.theatres.homepage.TermsandConditions
import com.influx.marcus.theatres.myaccount.EnrollGiftCard
import com.influx.marcus.theatres.myaccount.MyAccountVM
import com.influx.marcus.theatres.showtime.ShowtimeActivity
import com.influx.marcus.theatres.theatres.TheatreShowTimeActivity
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.CounterClass
import com.influx.marcus.theatres.utils.LogUtils
import com.influx.marcus.theatres.utils.UtilsDialog
import com.viewpagerindicator.CirclePageIndicator
import kotlinx.android.synthetic.main.payment_layout.*
import org.jetbrains.anko.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.UnsupportedEncodingException
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList


class PaymentScreen : AppCompatActivity(), CounterClass.CountdownTick {
    lateinit var toolbar: Toolbar
    private lateinit var context: Context
    lateinit var viewpagerCard: ViewPager
    lateinit var indicator: CirclePageIndicator
    lateinit var txtPay: TextView
    lateinit var imgBack: ImageView
    var isSavedCardsVisible: Boolean = false
    var isGiftCardsVisible = false
    var reducedRewardAmt: String = "0.00"
    var reducedGiftAmt: String = "0.00"
    var save = 0
    var isSave = false
    var isPaywithCard = false
    var isPaywithSavedCard = false
    var isTerms: Boolean = false
    var isCreditCardLayoutVisible: Boolean = false
    var isLoyaltyPay: Boolean = false
    var isGiftPay = false
    var loyaltyPayVal = 0
    var creditCardPayVal = 0
    var giftPayVal = 0
    var giftCardSize = 0
    var cumulativePoints: Float = 0.00f
    var loyaltyCard: String = ""
    var selectedMonth: Int = 12
    var selectedYear: Int = 12
    var isRewardCancel: Boolean = false
    var isGiftCancel: Boolean = false
    lateinit var ticketAmntGift: String
    lateinit var ticketAmntRewards: String
    lateinit var blockList: DATA
    lateinit var paymentVM: PaymentVM
    lateinit var myAccountVM: MyAccountVM
    private var isCardNumberValid = false
    var paymentConstants: PaymentConstants = PaymentConstants()
    lateinit var confirmationVM: ConfirmationVM
    lateinit var cardinfo: List<CardDetail>
    lateinit var cardTypes: List<String>
    private var savedCardsList: ArrayList<SavedCard> = ArrayList()
    private var giftCards: ArrayList<GiftCard> = ArrayList()
    var CardArraylist = ArrayList<String>()
    var giftArraylist = ArrayList<String>()
    var giftPinNo: String = ""
    var giftCardNo: String = ""
    var year: String = ""
    var month: String = ""
    var rewardVal = ""
    var giftVal = ""
    var pointsVal = ""
    var rewardDollarVal: Float = 0.00f
    var GiftDollarVal: Float = 0.00f
    var orderid: String = ""
    var cardno: String = ""
    var cardtype: String = ""
    var fname: String = ""
    var totalAmttoPay = 0.00f
    var lname: String = ""
    lateinit var cardDetails: LinearLayout
    private lateinit var recSavedCard: RecyclerView
    lateinit var sheetBehavior: BottomSheetBehavior<*>
    private lateinit var dataAdapterSavedcards: SavedCardAdapter
    lateinit var icClose: ImageView
    lateinit var blockresp: BlockSeatResp
    private var myp: MonthYearPicker? = null
    private var issuer: String? = null
    private var deletedCardPostition = 0
    var df = DecimalFormat("0.00")

    private val webApi = RestClient.getApiClient()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.payment_layout)
        PaymentObservers()
        confirmationObserver()
        initViews()
    }

    fun initViews() {
        context = this@PaymentScreen
        recSavedCard = findViewById(R.id.rvSavedCards)
        selectedYear = Calendar.getInstance().get(Calendar.YEAR)
        selectedMonth = Calendar.getInstance().get(Calendar.MONTH)
        //  icClose = findViewById(R.id.icClose)
        cardDetails = findViewById(R.id.cardDetails)
        rvSavedCards.setHasFixedSize(true)
        rvSavedCards.setLayoutManager(LinearLayoutManager(context, LinearLayoutManager.VERTICAL,
                false))
        imgBack = findViewById(R.id.ivBackToolbar)
        editCard.setSelection(editCard.length());
        // sheetBehavior = BottomSheetBehavior.from(llBreakup)

        tvTerms.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG)
        tvPrivacy.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG)
        blockresp = AppConstants.getObject(AppConstants.KEY_BLOCKRESP, this@PaymentScreen, BlockSeatResp::class.java as Class<Any>) as BlockSeatResp
        orderid = blockresp.DATA.order_id
        Log.i("Order Id", blockresp.DATA.order_id)
        AppConstants.putString(AppConstants.KEY_CVV, "", context)
        AppConstants.putString(AppConstants.KEY_ZIPCODE, "", context)
        cardchangelistener()
        CounterClass.setListener(this)
        ivDetail.setOnClickListener {
            carddetailpopup()
        }
        ivDownArrow.setOnClickListener {
            reviewpopup()
        }
        tvAmount.setOnClickListener {
            reviewpopup()
        }
        tvPrivacy.setOnClickListener {
            val i = Intent(context, PrivacyPolicy::class.java)
            startActivity(i)
            overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
        }
        if (!AppConstants.getString(AppConstants.KEY_GUEST_USER, context).equals("")) {
            cardDetails.visibility = View.VISIBLE
            ivDropDown.setImageResource(R.drawable.uparrow)
            llSave.visibility = View.GONE
            llGiftCard.visibility = View.GONE
            creditCardPayVal = 1
            isPaywithCard = true
        }

        if (intent.hasExtra("fromGiftEnroll")) {
            llgiftCardDetail.visibility = View.VISIBLE
            ivDropDown.setImageResource(R.drawable.uparrow)
            giftPayVal = 1
            isGiftCardsVisible = true
        }
        llPaymentType.setOnClickListener {
            if (isCreditCardLayoutVisible == true) {
                isCreditCardLayoutVisible = false
                ivDropDown.setImageResource(R.drawable.downarrow)
                isPaywithCard = false
                creditCardPayVal = 0
                cardDetails.visibility = View.INVISIBLE
            } else {
                isCreditCardLayoutVisible = true
                isPaywithCard = true
                isPaywithSavedCard = false
                creditCardPayVal = 1
                ivDropDown.setImageResource(R.drawable.uparrow)
                rvSavedCards.visibility = View.GONE
                cardDetails.visibility = View.VISIBLE
            }
            if (savedCardsList != null)
                for (eachitem in savedCardsList) {
                    eachitem.isSelect = false
                }
            try {
                dataAdapterSavedcards.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        llGiftCard.setOnClickListener {

            if (isGiftCardsVisible == false) {
                if (giftCardSize > 0) {
                    llgiftCardDetail.visibility = View.VISIBLE
                    clAddGiftCard.visibility = View.GONE
                } else {
                    clAddGiftCard.visibility = View.VISIBLE
                    llgiftCardDetail.visibility = View.GONE
                }
                isGiftCardsVisible = true
                ivGDropDown.setImageResource(R.drawable.uparrow)
            } else {
                clAddGiftCard.visibility = View.GONE
                llgiftCardDetail.visibility = View.GONE
                isGiftCardsVisible = false
                ivGDropDown.setImageResource(R.drawable.downarrow)
            }
        }
//if user have giftcard then we go via this button
        btAddGiftCard.setOnClickListener {
            val i = Intent(this@PaymentScreen, EnrollGiftCard::class.java)
            AppConstants.isFromPayment = true
            startActivity(i)
            overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
        }
        //if user dont have gift card then we go via this button
        btAddGiftcard.setOnClickListener {
            val i = Intent(this@PaymentScreen, EnrollGiftCard::class.java)
            AppConstants.isFromPayment = true
            startActivity(i)
            overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
        }




        tvTerms.setOnClickListener {
            val i = Intent(context, TermsandConditions::class.java)
            startActivity(i)
            overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
        }

        val OnCatSpinnerCL = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                (parent.getChildAt(0) as TextView).setTextColor(Color.BLUE)
                (parent.getChildAt(0) as TextView).textSize = 5f
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        cbSave.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                save = 1

            } else {
                save = 0
            }

        }
        cbTerms.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                isTerms = true
            } else {
                isTerms = false
            }
        }

        etExpiry.setOnClickListener {
            mnthyear()
            myp!!.show()
        }

        llSavedCards.setOnClickListener {

            if (isSavedCardsVisible == false) {
                isSavedCardsVisible = true
                isPaywithSavedCard = true
                isPaywithCard = false
                creditCardPayVal = 1
                ivSDropDown.setImageResource(R.drawable.uparrow)
                rvSavedCards.visibility = View.VISIBLE
                cardDetails.visibility = View.GONE
                ivDropDown.setImageResource(R.drawable.downarrow)
            } else {
                ivSDropDown.setImageResource(R.drawable.downarrow)
                isSavedCardsVisible = false
                isPaywithSavedCard = false
                creditCardPayVal = 1
                rvSavedCards.visibility = View.GONE

            }
        }
        btGiftPay.setOnClickListener {
            try {
                if (isGiftCancel == false) {
                    ticketAmntGift = tvAmount.text.toString()
                    ticketAmntGift = ticketAmntGift.replace("$", "")
                    val floatTicketAmt = ticketAmntGift.toFloat()
                    val floatGiftAmt = GiftDollarVal
                    if (floatTicketAmt > 0) {
                        if (floatGiftAmt > 0) {
                            btGiftPay.setText("Cancel")
                            btAddGiftCard.visibility = View.GONE
                            isGiftCancel = true
                            isGiftPay = true
                            giftPayVal = 1
                            if (floatGiftAmt > floatTicketAmt) {
                                val amt = floatGiftAmt - floatTicketAmt
                                val Amount = df.format(amt)
                                tvgiftDollar.setText("$" + Amount.toString())
                                tvAmount.text = "$0.00"
                                reducedGiftAmt = (floatGiftAmt - amt).toString()
                                creditCardPayVal = 0
                                isPaywithCard = false
                            } else {
                                val amt = floatTicketAmt - floatGiftAmt
                                tvgiftDollar.text = "$0.00"
                                reducedGiftAmt = floatGiftAmt.toString()
                                val Amount = df.format(amt)
                                tvAmount.text = "$" + Amount.toString()

                            }
                            cumulativePoints = reducedRewardAmt.toFloat() + reducedGiftAmt.toFloat()
                        } else {
                            alert("Insufficient balance in your gift card",
                                    getString(R.string.marcus_theatre_title)) {
                                positiveButton("OK") {
                                    it.dismiss()
                                }
                            }.show()
                        }
                    } else {

                    }
                } else {
                    isGiftCancel = false
                    isGiftPay = false
                    giftPayVal = 0
                    btAddGiftCard.visibility = View.VISIBLE
                    btGiftPay.setText("Pay")
                    if (isLoyaltyPay == false) {
                        tvAmount.text = "$" + blockList.receipt.total
                    } else {
                        cumulativePoints = cumulativePoints - reducedGiftAmt.toFloat()
                        val amnt = blockList.receipt.total.toFloat() - cumulativePoints
                        tvAmount.text = "$" + amnt.toString()

                    }
                    tvgiftDollar.setText(giftVal)
                }
            } catch (e: Exception) {
                Log.i("payment", "crash")
            }
        }
        btPay.setOnClickListener {
            try {
                if (isRewardCancel == false) {
                    ticketAmntRewards = tvAmount.text.toString()
                    ticketAmntRewards = ticketAmntRewards.replace("$", "")
                    val floatTicketAmt = ticketAmntRewards.toFloat()
                    val floatLoyalAmt = rewardDollarVal
                    if (floatTicketAmt > 0) {
                        if (floatLoyalAmt > 0) {
                            isRewardCancel = true
                            btPay.setText("Cancel")
                            isLoyaltyPay = true
                            loyaltyPayVal = 1
                            if (floatLoyalAmt > floatTicketAmt) {
                                val amt = floatLoyalAmt - floatTicketAmt
                                val Amount = df.format(amt)
                                tvDollar.text = "$" + Amount.toString()
                                tvAmount.text = "$0.00"
                                creditCardPayVal = 0
                                isPaywithCard = false
                                reducedRewardAmt = (floatLoyalAmt - amt).toString()

                            } else {
                                val amt = floatTicketAmt - floatLoyalAmt
                                tvDollar.text = "$0.00"
                                reducedRewardAmt = floatLoyalAmt.toString()
                                val Amount = df.format(amt)
                                tvAmount.text = "$" + Amount.toString()
                            }
                            cumulativePoints = reducedRewardAmt.toFloat() + reducedGiftAmt.toFloat()
                        } else {
                            alert("Insufficient balance in your rewards",
                                    getString(R.string.marcus_theatre_title)) {
                                positiveButton("OK") {
                                    it.dismiss()
                                }
                            }.show()
                        }
                    } else {

                    }
                } else {
                    isRewardCancel = false
                    isLoyaltyPay = false
                    loyaltyPayVal = 0
                    btPay.setText("Pay")
                    if (isGiftPay == false) {
                        tvAmount.text = "$" + blockList.receipt.total
                    } else {
                        cumulativePoints = cumulativePoints - reducedRewardAmt.toFloat()
                        val amnt = blockList.receipt.total.toFloat() - cumulativePoints
                        tvAmount.text = "$" + amnt.toString()
                    }
                    tvDollar.setText(rewardVal)
                }
            } catch (e: Exception) {
                Log.i("payment", "crash")
            }
        }

        tvProceed.setOnClickListener {
            var amntToPay = tvAmount.text.toString()
            if (amntToPay == "$0.00" && isPaywithSavedCard == false && isPaywithCard == false && isLoyaltyPay == false && isGiftPay == false) {
                alert(getString(R.string.select_payment_method), getString(R.string.marcus_theatre_title)) {
                    positiveButton("OK") {
                        it.dismiss()
                    }
                }.show()

            } else if (isPaywithSavedCard) {
                savedCardvalidate()
            } else if (isPaywithCard) {
                creditCardvalidate()
            } else if (isLoyaltyPay) {
                if (amntToPay != "$0.00") {
                    if (isPaywithCard || isPaywithSavedCard) {
                        rewardPayment()
                    } else {
                        alert(getString(R.string.select_payment_method), getString(R.string.marcus_theatre_title)) {
                            positiveButton("OK") {
                                it.dismiss()
                            }
                        }.show()
                    }
                } else {
                    rewardPayment()
                }
            } else if (isGiftPay) {
                if (amntToPay != "$0.00") {
                    if (isPaywithCard || isPaywithSavedCard) {
                        rewardPayment()
                    } else {
                        alert(getString(R.string.select_payment_method), getString(R.string.marcus_theatre_title)) {
                            positiveButton("OK") {
                                it.dismiss()
                            }
                        }.show()
                    }
                } else {
                    rewardPayment()
                }
            } else {
                alert(getString(R.string.select_payment_method), getString(R.string.marcus_theatre_title)) {
                    positiveButton("OK") {
                        it.dismiss()
                    }
                }.show()
            }

        }


        cardSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selected = parent!!.getItemAtPosition(position) as String
                val request = CardDetailReq(selected, AppConstants.APP_VERSION, AppConstants.APP_PLATFORM)
                loyaltyCard = selected
                if (UtilsDialog.isNetworkStatusAvialable(context)) {
                    UtilsDialog.showProgressDialog(context, "")
                    paymentVM.getCardDetail(request)
                }
            }
        }

        giftCardSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val selected = parent!!.getItemAtPosition(position) as String
                for (each in giftCards) {
                    if (each.card_no.equals(selected)) {
                        giftPinNo = each.pin
                        giftCardNo = each.card_no
                    }
                }
                val request = GiftCardBalanceReq(selected, giftPinNo, AppConstants.APP_VERSION, AppConstants.APP_PLATFORM)
                giftCardNo = selected
                if (UtilsDialog.isNetworkStatusAvialable(context)) {
                    UtilsDialog.showProgressDialog(context, "")
                    myAccountVM.getBalanceGiftCardResponse(request)
                }
            }
        }

        setToolbar()
        blockList = blockresp.DATA
        tvAmount.text = "$" + blockList.receipt.total
        totalAmttoPay = blockList.receipt.total.toFloat()

        val request = PaymentCardListReq(AppConstants.getString(AppConstants.KEY_USERID, context),
                AppConstants.APP_VERSION, AppConstants.APP_PLATFORM)
        if (UtilsDialog.isNetworkStatusAvialable(context)) {
            UtilsDialog.showProgressDialog(context, "")
            paymentVM.getPaymentCardList(request)
        }

        /*  ivDownArrow.setOnClickListener {
              if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                  sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
              } else {
                  sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
              }
          }*/

    }


    private fun setToolbar() {
        imgBack.setOnClickListener({ onBackPressed() })
    }


    fun setCardAdapter() {
        var cardAdapter = CardViewPagerAdapter(context)
        viewpagerCard.adapter = cardAdapter
        indicator.setViewPager(viewpagerCard)
    }

    fun creditCardvalidate() {

        val cardNumber = editCard.getText().toString()
        val cardCVV = etCvv.getText().toString()

        if (!TextUtils.isEmpty(etExpiry.getText().toString().trim({ it <= ' ' }))) {
            val timeSplit = etExpiry.getText().toString().split("/".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
            month = timeSplit[0]
            year = timeSplit[1]
        }

        inputCardNo.isErrorEnabled = false
        inputExpiry.isErrorEnabled = false
        inputCvv.isErrorEnabled = false
        inputAVS.isErrorEnabled = false
        inputFname.isErrorEnabled = false
        inputLName.isErrorEnabled = false

        if (editCard.text.isEmpty() && !isCardNumberValid) {
            inputCardNo.isErrorEnabled = true
            inputCardNo.error = "Please enter Card No"
        } else if (etExpiry.text.isEmpty()) {
            inputExpiry.isErrorEnabled = true
            inputExpiry.error = "Please enter Expire Date"
        } else if (etCvv.text.isEmpty()) {
            inputCvv.isErrorEnabled = true
            inputCvv.error = "Please enter CVV"
        } else if (etAVS.text.isEmpty()) {
            inputAVS.isErrorEnabled = true
            inputAVS.error = "Please enter ZIP"
        } else if (etFirstName.text.isEmpty()) {
            inputFname.isErrorEnabled = true
            inputFname.error = "Please enter First Name"
        } else if (etLName.text.isEmpty()) {
            inputLName.isErrorEnabled = true
            inputLName.error = "Please enter Last Name"
        } else {
            try {
                if (!(cardTypes.contains(issuer))) {
                    alert(getString(R.string.cardtype_not_allowed),
                            getString(R.string.marcus_theatre_title)) {
                        positiveButton("OK") {
                            it.dismiss()
                        }
                    }.show()
                } else {
                    if (isTerms == false) {
                        alert(getString(R.string.accept_terms_condition)) {
                            positiveButton("OK") {
                                it.dismiss()
                            }
                        }.show()
                    } else {
                        val request = BookingConfirmationReq(AppConstants.getString(AppConstants.KEY_USERID, context),
                                blockresp.DATA.order_id, creditCardPayVal.toString(),
                                AppConstants.getString(AppConstants.KEY_LOYALTYCARDNO, context), loyaltyPayVal.toString(), etFirstName.text.toString(),
                                etLName.text.toString(), AppConstants.getString(AppConstants.KEY_EMAIL, context), editCard.text.toString(),
                                month, year, etCvv.text.toString(), etAVS.text.toString(), issuer.toString(), save.toString(), giftCardNo, giftPinNo, giftPayVal.toString(),
                                AppConstants.APP_VERSION, AppConstants.APP_PLATFORM)
                        if (UtilsDialog.isNetworkStatusAvialable(context)) {
                            UtilsDialog.showProgressDialog(context, "")
                            confirmationVM.getBookingConfirmationResponse(request)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun reviewpopup() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val window = dialog.window
        dialog.setContentView(R.layout.payment_popup_layout)
        window!!.setGravity(Gravity.BOTTOM)
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        dialog.getWindow().setBackgroundDrawable(ColorDrawable(Color.BLACK));
        dialog.setCancelable(false)

        // val m_inflater = LayoutInflater.from(this@PaymentScreen)
        val icClose = dialog!!.findViewById<ImageView>(R.id.icClose)
        val tvTotalAmount = dialog!!.findViewById<TextView>(R.id.tvTotalAmount)
        val tvRewardsAmount = dialog!!.findViewById<TextView>(R.id.tvRewardsAmount)
        val tvRewards = dialog!!.findViewById<TextView>(R.id.tvRewards)
        val tvGiftCard = dialog!!.findViewById<TextView>(R.id.tvGiftCard)
        val tvGiftCardAmount = dialog!!.findViewById<TextView>(R.id.tvGiftCardAmount)
        val rvTicketTypes = dialog!!.findViewById<RecyclerView>(R.id.rvTicketTypes) as RecyclerView
        if (isLoyaltyPay) {
            try {
                var rewardAmnt = reducedRewardAmt.toFloat()
                val Amount = df.format(rewardAmnt)
                tvRewardsAmount!!.setText("(-) $" + Amount)

                tvRewards!!.visibility = View.VISIBLE
                tvRewardsAmount.visibility = View.VISIBLE
            } catch (e: Exception) {
                Log.i("popup", "crash")
            }

        }
        if (isGiftPay) {
            try {
                var giftAmnt = reducedGiftAmt.toFloat()
                val Amount = df.format(giftAmnt)
                tvGiftCardAmount!!.setText("(-) $" + Amount)

                tvGiftCard!!.visibility = View.VISIBLE
                tvGiftCardAmount!!.visibility = View.VISIBLE
            } catch (e: Exception) {
                Log.i("popup", "crash")
            }

        }
        rvTicketTypes.setHasFixedSize(true)
        dialog.setCanceledOnTouchOutside(false)
        rvTicketTypes.setLayoutManager(LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false))
        icClose!!.setOnClickListener {
            dialog.dismiss()
        }
        val mAdapter = com.influx.marcus.theatres.payment.TicketTypeAdapter(blockList, this@PaymentScreen)
        rvTicketTypes.adapter = mAdapter
        tvTotalAmount!!.setText(tvAmount.text.toString())
        dialog.show();
    }

    fun carddetailpopup() {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.rewards_popup_layout);
        dialog.getWindow().setLayout(matchParent, wrapContent);
        dialog.getWindow().setGravity(Gravity.TOP);
        dialog.getWindow().setBackgroundDrawable(ColorDrawable(Color.BLACK));

        val m_inflater = LayoutInflater.from(this@PaymentScreen)
        val icClose = dialog!!.findViewById<ImageView>(R.id.icClose)
        val tvPoints = dialog!!.findViewById<TextView>(R.id.tvPoints)
        val tvRewards = dialog!!.findViewById<TextView>(R.id.tvRewards)
        tvPoints.text = pointsVal
        tvRewards.text = tvDollar.text.toString()

        icClose.setOnClickListener {
            dialog.dismiss()
        }
        //  val tvTotalAmount = dialog!!.findViewById<TextView>(R.id.tvTotalAmount)
        // tvTotalAmount.setText(blockList.receipt.total)
        dialog.show();
    }

    private fun PaymentObservers() {
        paymentVM = ViewModelProviders.of(this).get(PaymentVM::class.java)
        myAccountVM = ViewModelProviders.of(this).get(MyAccountVM::class.java)
        paymentVM.getPaymentCardListDetails().observe(this, paymentObs)
        paymentVM.getCancelBookingDetails().observe(this, paymentCancelObs)
        paymentVM.getApiErrorDetails().observe(this, errorObs)
        paymentVM.getCardDetail().observe(this, cardDetailObs)
        paymentVM.getDeleteSavedCardResp().observe(this, deleteSavedCardObs)
        myAccountVM.getBalanceGiftData().observe(this, giftCardDetailObs)

    }

    private var deleteSavedCardObs = object : Observer<DeleteSavedCardResp> {
        override fun onChanged(t: DeleteSavedCardResp?) {
            if (t!!.STATUS) {
                savedCardsList.removeAt(deletedCardPostition)
                if (savedCardsList.size == 0) {
                    alert(getString(R.string.no_saved_cards), getString(R.string.marcus_theatre_title)) {
                        positiveButton("OK") {
                            it.dismiss()
                        }
                    }.show()
                    llSavedCards.visibility = View.GONE
                }
                dataAdapterSavedcards.notifyDataSetChanged()
                UtilsDialog.hideProgress()
            } else {
                alert(t!!.DATA.message, getString(R.string.marcus_theatre_title)) {
                    positiveButton("OK") {
                        it.dismiss()
                    }
                }.show()
                LogUtils.d("DeleteSavedCard", "saved card status false")
                UtilsDialog.hideProgress()
            }
        }
    }

    private var errorObs = object : Observer<String> {
        override fun onChanged(t: String?) {
            UtilsDialog.hideProgress()
            alert(getString(R.string.ohinternalservererror), getString(R.string.marcus_theatre_title)) {
                positiveButton("OK") {
                    it.dismiss()
                }
            }.show()
        }
    }

    private var cardDetailObs = object : Observer<CardDetailResp> {
        @SuppressLint("ResourceAsColor")
        override fun onChanged(t: CardDetailResp?) {
            UtilsDialog.hideProgress()
            rewardVal = t!!.DATA.dollars_in_text
            pointsVal = t!!.DATA.points
            tvDollar.setText(rewardVal)
            rewardDollarVal = (t!!.DATA.dollars).toFloat()


        }
    }
    private var giftCardDetailObs = object : Observer<GiftCardBalanceResp> {
        @SuppressLint("ResourceAsColor")
        override fun onChanged(t: GiftCardBalanceResp?) {
            UtilsDialog.hideProgress()
            tvgiftDollar.setText(t!!.DATA.balanceInString)
            GiftDollarVal = t!!.DATA.balance.toFloat()
            giftVal = t!!.DATA.balanceInString

        }
    }

    private var paymentCancelObs = object : Observer<CancelBookingResp> {
        override fun onChanged(t: CancelBookingResp?) {
            // toast(t!!)
        }
    }


    private var paymentObs = object : Observer<PaymentCardListResp> {
        override fun onChanged(t: PaymentCardListResp?) {
            UtilsDialog.hideProgress()

            if (t!!.STATUS == true) {
                // if(t.DATA.reward_details.equals())
                if (t!!.DATA!!.reward_details!!.card_details != null) {
                    // llMagical.visibility =View.VISIBLE
                    cardinfo = t!!.DATA!!.reward_details!!.card_details
                    rewardVal = cardinfo.get(0).dollars_in_text
                    pointsVal = cardinfo.get(0).points
                    try {
                        var dollarAmountBalance = (t.DATA.reward_details.card_details.get(0).dollars).toFloat()
                        if (dollarAmountBalance > 0) {
                            //do nothing
                            //  btPay.isClickable=true
                            //  btPay.setBackgroundColor(context.resources.getColor(R.color.marcus_red))
                        } else {// disable pay button
                            btPay.isClickable = false
                            btPay.alpha = 0.5f
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    tvDollar.setText(rewardVal)
                    for (eachCardlist in cardinfo) {
                        CardArraylist.add(eachCardlist.card_number)
                    }
                    val dataAdapter = ArrayAdapter(this@PaymentScreen, android.R.layout.simple_spinner_item, CardArraylist)
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    cardSpinner.setAdapter(dataAdapter)
                    llMagical.visibility = View.VISIBLE
                }
                if (t!!.DATA!!.gift_cards != null && t!!.DATA!!.gift_cards.size > 0) {
                    giftCardSize = t!!.DATA!!.gift_cards.size
                    giftCards.addAll(t!!.DATA!!.gift_cards)
                    for (eachcardlist in giftCards) {
                        giftArraylist.add(eachcardlist.card_no)
                    }
                    val dataAdapter = ArrayAdapter(this@PaymentScreen, android.R.layout.simple_spinner_item, giftArraylist)
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    giftCardSpinner.setAdapter(dataAdapter)

                } else {

                }
                if (t!!.DATA!!.saved_cards != null && t!!.DATA!!.saved_cards.size > 0) {
                    llSavedCards.visibility = View.VISIBLE
                    savedCardsList.addAll(t!!.DATA!!.saved_cards)
                    dataAdapterSavedcards = SavedCardAdapter(savedCardsList, savecardListener, context)
                    rvSavedCards.setAdapter(dataAdapterSavedcards)
                }
                cardTypes = t!!.DATA!!.allowed_card_types

            } else {
                alert(t!!.DATA.message, getString(R.string.marcus_theatre_title)) {
                    positiveButton("OK") {
                        it.dismiss()
                    }
                }.show()
            }
            rrPayment.visibility = View.VISIBLE
        }
    }

    private fun confirmationObserver() {
        confirmationVM = ViewModelProviders.of(this).get(ConfirmationVM::class.java)
        confirmationVM.getConfirmationRespData().observe(this, confirmationObs)
        confirmationVM.getApiErrorData().observe(this, errorObs)
    }

    private var confirmationObs = object : Observer<BookingConfirmationResp> {
        override fun onChanged(t: BookingConfirmationResp?) {
            UtilsDialog.hideProgress()
            if (t!!.STATUS == true) {
                val i = Intent(this@PaymentScreen, BookingConfirmation::class.java)
                i.putExtra("success", true)
                i.putExtra("date", t!!.DATA.show_date)
                i.putExtra("movieimg", t.DATA.movie_image)
                i.putExtra("expimg", t.DATA.experience_img)
                if (t.DATA.exprience_single_logo != null)
                    i.putExtra("exprience_single_logo", t.DATA.exprience_single_logo)
                i.putExtra("movietitle", t!!.DATA.movie_title)
                i.putExtra("expTitle", t!!.DATA.experience_title)
                i.putExtra("cinemaname", t.DATA.cinema_name)
                i.putExtra("qrcode", t!!.DATA.qrcode)
                i.putExtra("qrtext", t!!.DATA.qrcode_text)
                i.putExtra("bookingid", t!!.DATA.booking_id)
                i.putExtra("ticketCount", t!!.DATA.ticketCount)
                i.putExtra("screen", t!!.DATA.screen_name)
                i.putExtra("showtime", t!!.DATA.show_time)
                startActivity(i)
                overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
                //ivImage
            }/* else {
                if (t!!.DATA.failure.isNullOrBlank()) {
                    alert(t.DATA!!.message, "Marcus Theatres")
                    {
                        positiveButton("OK") { dialog ->
                            finish();
                            startActivity(getIntent());
                            dialog.dismiss()
                        }
                    }.show().setCancelable(false)

                }*/ else {
                if (t!!.DATA.failure == "true") {
                    val i = Intent(this@PaymentScreen, BookingConfirmation::class.java)
                    i.putExtra("success", false)
                    i.putExtra("movieimg", t.DATA.booking_data.movie_image)
                    i.putExtra("date", t!!.DATA.booking_data.show_date)
                    i.putExtra("expimg", t.DATA.booking_data.experience_img)
                    if (t.DATA.booking_data.exprience_single_logo != null)
                        i.putExtra("exprience_single_logo", t.DATA.booking_data.exprience_single_logo)
                    i.putExtra("movietitle", t!!.DATA.booking_data.movie_title)
                    i.putExtra("expTitle", t!!.DATA.booking_data.experience_title)
                    i.putExtra("cinemaname", t.DATA.booking_data.cinema_name)
                    i.putExtra("showtime", t!!.DATA.booking_data.show_time)
                    i.putExtra("fname", etFirstName.text.toString())
                    i.putExtra("screen", t!!.DATA.booking_data.screen_name)
                    startActivity(i)
                    overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
                }
            }


        }
    }

    fun navigateToconfirmation() {
        startActivity(Intent(context, BookingConfirmation::class.java))
        finish()
    }

    fun mnthyear() {


        myp = MonthYearPicker(this@PaymentScreen)
        val currentyear = Calendar.getInstance().get(Calendar.YEAR)
        val currentmonth = Calendar.getInstance().get(Calendar.MONTH)
        myp!!.build(selectedMonth - 1, selectedYear, DialogInterface.OnClickListener { dialog, which ->
            selectedYear = (myp!!.getSelectedYear())
            selectedMonth = myp!!.getSelectedMonth() + 1
            if (selectedMonth < 10) {
                etExpiry.setText("0" + selectedMonth.toString() + "/" + selectedYear.toString())

            } else {
                etExpiry.setText(selectedMonth.toString() + "/" + selectedYear.toString())
            }
            /*   Log.d("calendar", "onClick: current month : " + currentmonth +
                       " and year is : " + currentyear + " selected month : "
                       + myp!!.getSelectedMonth() + " and selected year " + selectedYear)*/
            if (selectedYear.toString().equals(currentyear.toString(), ignoreCase = true)) {
                if (currentmonth > myp!!.getSelectedMonth()) {
                    etExpiry.setText("")
                    alert("Cannot select previous month/year", getString(R.string.marcus_theatre_title)) {
                        positiveButton("OK") {
                            it.dismiss()
                        }
                    }.show()

                } else {
                    // Logger.d(TAG, "onClick: all valid month year")
                }
            } else {
                // Logger.d(TAG, "onClick: selected year and current year are diffrent")
            }
        }, null)
    }

    fun cardchangelistener() {
        editCard.addTextChangedListener(object : TextWatcher {
            var image: Int = 0
            var cardLength: Int = 0
            var setSpacesIndex = 4
            val ccNumber = ""
            var afterTextPosition: Int = 0
            val space = ' '
            var currentPosition: Int = 0
            var len = 0

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.length > 0) { // to confirm rupay card we need min 6 digit.
                    issuer = paymentConstants.getIssuer(charSequence.toString().replace(" ", ""))

                    if (issuer != null && issuer!!.length > 1) {
                        isCardNumberValid = true
                        image = getIssuerImage(issuer!!)
                        ivCardImg.setImageResource(image)
                        /* if (issuer === "AMEX")
                            // editCard.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(4)))
                         else
                           //  editCard.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(3)))
                         if (issuer === "SMAE" || issuer === "MAES") {
                             editCard.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(23)))
                             cardLength = 23
                         } else if (issuer === "AMEX") {
                             editCard.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(18)))
                             cardLength = 18
                         } else if (issuer === "DINR") {
                             editCard.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(17)))
                             cardLength = 17
                         } else {
                             editCard.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(19)))
                             cardLength = 20
                         }*/
                    }
                } else {
                    issuer = null
                    ivCardImg.setImageResource(R.drawable.icon_card)
                    etCvv.getText().clear()
                }
            }

            override fun afterTextChanged(s: Editable) {
                if (s.length > 0 && s.length % 5 == 0) {
                    val c = s[s.length - 1]
                    if (space == c) {
                        s.delete(s.length - 1, s.length)
                    }
                }
                // Insert char where needed.
                if (s.length > 0 && s.length % 5 == 0) {
                    val c = s[s.length - 1]
                    // Only if its a digit where there should be a space we insert a space
                    if (Character.isDigit(c) && TextUtils.split(s.toString(), space.toString()).size <= 3) {
                        s.insert(s.length - 1, space.toString())
                    }
                }
                if (s.length > 0) {
                    editCard.isCursorVisible = false
                } else {
                    editCard.isCursorVisible = true

                }

            }

        })

        editCard.setOnFocusChangeListener(View.OnFocusChangeListener { view, b ->
            if (!b) {
                cardValidation()
            }
        })
    }


    fun cardValidation() {

        if (!paymentConstants.validateCardNumber(editCard.getText().toString().replace(" ", "")) && editCard.length() > 0) {
            ivCardImg.setImageResource(R.drawable.error_icon)
            isCardNumberValid = false
            // uiValidation();
        } else if (paymentConstants.validateCardNumber(editCard.getText().toString().replace(" ", "")) && editCard.length() > 0) {
            isCardNumberValid = true
            /*  if (mPaymentParams.getOfferKey() != null && null != mPaymentParams.getUserCredentials())
                getOfferStatus();*/
            //            uiValidation();
        } else {
            ivCardImg.setImageResource(R.drawable.error_icon)
            isCardNumberValid = false

        }
    }


    private fun getIssuerImage(issuer: String): Int {

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            when (issuer) {
                "VISA" -> return R.drawable.logo_visa
                "LASER" -> return R.drawable.laser
                "DISCOVER" -> return R.drawable.discover
                "MAES" -> return R.drawable.mas_icon
                "MAST" -> return R.drawable.mc_icon
                "AMEX" -> return R.drawable.amex
                "DINR" -> return R.drawable.diner
                "JCB" -> return R.drawable.cross_cc
                "SMAE" -> return R.drawable.maestro
                "RUPAY" -> return R.drawable.rupay
            }
            return 0
        } else {

            when (issuer) {
                "VISA" -> return R.drawable.logo_visa
                "LASER" -> return R.drawable.laser
                "DISCOVER" -> return R.drawable.discover
                "MAES" -> return R.drawable.mas_icon
                "MAST" -> return R.drawable.mc_icon
                "AMEX" -> return R.drawable.amex
                "DINR" -> return R.drawable.diner
                "JCB" -> return R.drawable.cross_cc
                "SMAE" -> return R.drawable.maestro
                "RUPAY" -> return R.drawable.rupay
            }
            return 0
        }
    }

    override fun onBackPressed() {
        alert("Do you want to cancel your booking ?", "Marcus Theatres")
        {
            positiveButton("YES") { dialog ->
                if (CounterClass.getInstance() != null) CounterClass.getInstance().cancel()

                AppConstants.putObject(AppConstants.KEY_SEATDATA, "", context)
                AppConstants.putObject(AppConstants.KEY_BLOCKRESP, "", context)
                AppConstants.putObject(AppConstants.KEY_BLOCKREQ, "", context)
                AppConstants.putObject(AppConstants.KEY_UNRESERVE_BLOCKREQ, "", context)
                val request = CancelBookReq(AppConstants.getString(AppConstants.KEY_THEATREID, context),
                        AppConstants.getString(AppConstants.KEY_CID, context), AppConstants.getString(AppConstants.KEY_SALEID, context),
                        AppConstants.APP_VERSION, AppConstants.APP_PLATFORM)
                if (UtilsDialog.isNetworkStatusAvialable(context)) {
                    paymentVM.getCancelBook(request)
                }
                Log.i("block Resp", AppConstants.getString(AppConstants.KEY_SEATDATA, context))
                val fromdata = AppConstants.getString(AppConstants.KEY_SHOWTIME, context)
                when (fromdata) {
                    "theatre_showtime" -> {
                        val intentShowtime = Intent(context, TheatreShowTimeActivity::class.java)
                        AppConstants.putString(AppConstants.KEY_THEATRESHOWTIME, "payment", context)
                        intentShowtime.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intentShowtime)
                        finish()
                        overridePendingTransition(R.animator.slide_from_left , R.animator.slide_to_right)
                        dialog.dismiss()
                    }
                    "movie_showtime" -> {
                        fetchShowTimes()
                    }
                    else -> {
                        fetchShowTimes()
                    }
                }
            }
        }.show()
    }


    override fun onTick(secondsLeft: Long) {
        val minutes = secondsLeft / 60
        val seconds = secondsLeft % 60
        tvTimer.text = CounterClass.getFormatedTime()
        if (secondsLeft < 1) {
            // show timeout
            tvTimer.text = "00:00"
            tvTimer.clearAnimation()
            if (CounterClass.getInstance() != null) CounterClass.getInstance().cancel()
            if (!isFinishing) {
                //AppConstants.putString(AppConstants.KEY_THEATRESHOWTIME,"payment",context)

                alert("Your session has timed out", "Marcus Theatres")
                {
                    positiveButton("OK") { dialog ->

                        AppConstants.putObject(AppConstants.KEY_SEATDATA, "", context)
                        AppConstants.putObject(AppConstants.KEY_BLOCKRESP, "", context)
                        AppConstants.putObject(AppConstants.KEY_BLOCKREQ, "", context)
                        AppConstants.putObject(AppConstants.KEY_UNRESERVE_BLOCKREQ, "", context)
                        val request = CancelBookReq(AppConstants.getString(AppConstants.KEY_THEATREID, context),
                                AppConstants.getString(AppConstants.KEY_CID, context), AppConstants.getString(AppConstants.KEY_SALEID, context),
                                AppConstants.APP_VERSION, AppConstants.APP_PLATFORM)
                        if (UtilsDialog.isNetworkStatusAvialable(context)) {
//                            UtilsDialog.showProgressDialog(context, "")
//                            paymentVM.getCancelBook(request)
                        }
                        Log.i("block Resp", AppConstants.getString(AppConstants.KEY_SEATDATA, context))
                        val fromdata = AppConstants.getString(AppConstants.KEY_SHOWTIME, context)
                        when (fromdata) {
                            "theatre_showtime" -> {
                                val intentShowtime = Intent(context, TheatreShowTimeActivity::class.java)
                                AppConstants.putString(AppConstants.KEY_THEATRESHOWTIME, "payment", context)
                                intentShowtime.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intentShowtime)
                                finish()
                                overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
                                dialog.dismiss()
                            }
                            "movie_showtime" -> {
                                fetchShowTimes()
                            }
                            else -> {
                                fetchShowTimes()
                            }
                        }
                    }
                }.show().setCancelable(false)

            }

        } else if (secondsLeft.toInt() == 90) {
            val v: Vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                v.vibrate(500)
            }
        } else if (secondsLeft < 90) {
            blinkTimerText()
        }
    }

    var isTimerBlinking = false
    private fun blinkTimerText() {
        if (!isTimerBlinking) {
            val anim = AlphaAnimation(0.0f, 1.0f)
            anim.duration = 150 //You can manage the time of the blink with this parameter
            anim.startOffset = 20
            anim.repeatMode = Animation.REVERSE
            anim.repeatCount = Animation.INFINITE
            tvTimer.startAnimation(anim)
            isTimerBlinking = true
        }
    }

    private var savecardListener: SavedCardAdapter.cardAdapterListener = object : SavedCardAdapter.cardAdapterListener {
        override fun deleteCard(position: Int) {
            deletedCardPostition = position
            val userId = AppConstants.getString(AppConstants.KEY_USERID, context)
            val cardId = savedCardsList.get(position).card_id
            val reqDeleteSavedCardReq = DeleteSavedCardReq(
                    userId,
                    cardId,
                    AppConstants.APP_VERSION,
                    AppConstants.APP_PLATFORM
            )
            paymentVM.deleteThisSavedCard(reqDeleteSavedCardReq)
        }

        override fun savecarddetail(position: Int) {

            val item = savedCardsList.get(position)
            fname = item.first_name
            lname = item.last_name
            month = item.card_month
            year = item.card_year
            cardtype = item.card_type
            val decryptedString = decodeBase64(item.card_no)

            // to hide credit card layout if visible
        }

        override fun savecard(save: Boolean, pos: Int) {
            // to hide credit card layout if visible

            for (eachitem in savedCardsList) {
                eachitem.isSelect = false
            }
            savedCardsList.get(pos).isSelect = true
            if (savedCardsList.get(pos).isVisible) {
                savedCardsList.get(pos).isVisible = false
            } else {
                savedCardsList.get(pos).isVisible = true
            }

            if (save == true) {
                isSave = save
            } else {
                isSave = save
            }
            dataAdapterSavedcards.notifyDataSetChanged()
        }
    }

    private fun decodeBase64(coded: String): String {
        var valueDecoded = ByteArray(0)
        try {
            valueDecoded = Base64.decode(coded.toByteArray(charset("UTF-8")), Base64.DEFAULT)
        } catch (e: UnsupportedEncodingException) {
        }
        LogUtils.d("decoded string", valueDecoded.toString())

        cardno = String(valueDecoded)
        LogUtils.d("decoded string", cardno)

        return String(valueDecoded)

    }

    private fun savedCardvalidate() {
        if (isTerms == false) {
            alert("Please accept Terms and Conditions",
                    getString(R.string.marcus_theatre_title)) {
                positiveButton("OK") {
                    it.dismiss()
                }
            }.show()
        } else {
            if (AppConstants.getString(AppConstants.KEY_ZIPCODE, context).isNotEmpty()
                    && AppConstants.getString(AppConstants.KEY_ZIPCODE, context).isNotEmpty()) {
                val request = BookingConfirmationReq(AppConstants.getString(AppConstants.KEY_USERID, context),
                        blockresp.DATA.order_id, creditCardPayVal.toString(), AppConstants.getString(AppConstants.KEY_LOYALTYCARDNO, context),
                        loyaltyPayVal.toString(), fname, lname, AppConstants.getString(AppConstants.KEY_EMAIL, context), cardno,
                        month, year, AppConstants.getString(AppConstants.KEY_CVV, context),
                        AppConstants.getString(AppConstants.KEY_ZIPCODE, context), cardtype, save.toString(),
                        giftCardNo, giftPinNo, giftPayVal.toString(),
                        AppConstants.APP_VERSION, AppConstants.APP_PLATFORM)
                if (UtilsDialog.isNetworkStatusAvialable(context)) {
                    UtilsDialog.showProgressDialog(context, "")
                    confirmationVM.getBookingConfirmationResponse(request)
                }
            } else {
                alert("Please enter Zipcode",
                        getString(R.string.marcus_theatre_title)) {
                    positiveButton("OK") {
                        it.dismiss()
                    }
                }.show()

            }
        }
    }


    private fun rewardPayment() {
        if (isTerms == false) {
            alert("Please accept Terms and Conditions",
                    getString(R.string.marcus_theatre_title)) {
                positiveButton("OK") {
                    it.dismiss()
                }
            }.show()
        } else {
            val request = BookingConfirmationReq(AppConstants.getString(AppConstants.KEY_USERID, context),
                    blockresp.DATA.order_id, creditCardPayVal.toString(), loyaltyCard, loyaltyPayVal.toString(), fname,
                    lname, AppConstants.getString(AppConstants.KEY_EMAIL, context), cardno,
                    month, year, AppConstants.getString(AppConstants.KEY_CVV, context),
                    AppConstants.getString(AppConstants.KEY_ZIPCODE, context), cardtype, save.toString(), giftCardNo, giftPinNo, giftPayVal.toString(),
                    AppConstants.APP_VERSION, AppConstants.APP_PLATFORM)
            if (UtilsDialog.isNetworkStatusAvialable(context)) {
                UtilsDialog.showProgressDialog(context, "")
                confirmationVM.getBookingConfirmationResponse(request)
            }
        }
    }

    override fun onResume() {
        CounterClass.setListener(this)
        super.onResume()
    }

    fun fetchShowTimes() {
        if (UtilsDialog.isNetworkStatusAvialable(context)) {
            UtilsDialog.showProgressDialog(context, "")
            val request = ShowtimeRequest(
                    AppConstants.getString(AppConstants.KEY_STATE_CODE, context),
                    AppConstants.getString(AppConstants.KEY_LATITUDE, context),
                    AppConstants.getString(AppConstants.KEY_LONGITUDE, context),
                    AppConstants.getString(AppConstants.KEY_MOVIECODE, context),
                    AppConstants.getString(AppConstants.KEY_TMDBID, context),
                    AppConstants.getStringList(AppConstants.KEY_PREFEREDCINEMA, context))
            fetchShowtimeData(request)
        }
    }

    fun fetchShowtimeData(req: ShowtimeRequest) {
        val prefLocationCall: Call<ShowtimeResponse> =
                webApi.getShowtimes(AppConstants.AUTHORISATION_HEADER, req)
        prefLocationCall.enqueue(object : Callback<ShowtimeResponse> {
            override fun onFailure(call: Call<ShowtimeResponse>?, t: Throwable?) {
                UtilsDialog.hideProgress()
                alert("No Showtimes Available", getString(R.string.marcus_theatre_title)) {
                    positiveButton("OK") {
                        it.dismiss()
                        val intentShowtime = Intent(context, ShowtimeActivity::class.java)
                        intentShowtime.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        intentShowtime.putExtra("fromcancelledtxn", true)
                        startActivity(intentShowtime)
                        finish()
                        overridePendingTransition(R.animator.slide_from_left , R.animator.slide_to_right)                    }
                }.show()
            }

            override fun onResponse(call: Call<ShowtimeResponse>?, response: Response<ShowtimeResponse>?) {
                val dataitem = response!!.body()
                if (response!!.isSuccessful && dataitem!!.STATUS) {
                    doAsync {
                        AppConstants.putObject(AppConstants.KEY_SHOWTIMERESOBJ, dataitem, context)
                        uiThread {
                            val intentShowtime = Intent(context, ShowtimeActivity::class.java)
                            intentShowtime.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            intentShowtime.putExtra("prefetcheddata", true)
                            startActivity(intentShowtime)
                            finish()
                            overridePendingTransition(R.animator.slide_from_left , R.animator.slide_to_right)
                            UtilsDialog.hideProgress()
                        }
                    }
                } else {
                    UtilsDialog.hideProgress()
                    alert("No Showtimes Available", getString(R.string.marcus_theatre_title)) {
                        positiveButton("OK") {
                            it.dismiss()
                            val intentShowtime = Intent(context, ShowtimeActivity::class.java)
                            intentShowtime.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            intentShowtime.putExtra("fromcancelledtxn", true)
                            startActivity(intentShowtime)
                            finish()
                            overridePendingTransition(R.animator.slide_from_left , R.animator.slide_to_right)                        }
                    }.show()
                }
            }
        })
    }

}
