package com.influx.marcus.theatres.myaccount.bookinghistory

import android.app.DatePickerDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import android.widget.Toast
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.rewardcarddetail.RewardCardDetailReq
import com.influx.marcus.theatres.api.ApiModels.rewardcarddetail.RewardCardDetailResp
import com.influx.marcus.theatres.api.ApiModels.rewardtransaction.PointsHistory
import com.influx.marcus.theatres.api.ApiModels.rewardtransaction.RewardTransactionReq
import com.influx.marcus.theatres.api.ApiModels.rewardtransaction.RewardTransactionResp
import com.influx.marcus.theatres.api.ApiModels.rewardtransaction.RewardsHistory
import com.influx.marcus.theatres.myaccount.MyAccountVM
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.UtilsDialog
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_reward_detail.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList


class RewardDetailActivity : AppCompatActivity() {
    lateinit var myAccountVM: MyAccountVM
    var cardNo: String = ""
    lateinit var context: Context
    lateinit var termsUrl: String
    var isMagical = true
    var isTransaction = false
    var isterms = false
    var isPoints = true
    private lateinit var tvPoints: TextView
    private lateinit var tvRewards: TextView
    private lateinit var rewardHistory: List<RewardsHistory>
    private lateinit var pointsHistory: List<PointsHistory>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reward_detail)
        context = this@RewardDetailActivity
        myAccountObserver()
        initView()
    }

    private fun initView() {
        cardNo = intent.extras.getString("CardNo")
        ivBackic.setOnClickListener { onBackPressed() }
        tvFrom.setOnClickListener {
            selectDate("from")
        }
        tvTo.setOnClickListener {

            if (tvFrom.text.toString().isEmpty()) {
                alert("select from date",
                        getString(R.string.marcus_theatre_title)) {
                    positiveButton("OK") {
                        it.dismiss()
                    }
                }.show()
            } else {
                selectDate("to")
            }
        }
        if (isMagical) {
            isMagical = false
            clmagical.visibility = View.VISIBLE
            Picasso.with(context).load(R.drawable.uparrow).into(ivDropDown)
            ivDropDown.setColorFilter(ContextCompat.getColor(context, R.color.white));
            tvMagical.setTextColor(resources.getColor(R.color.white))
            val myCustomFont: Typeface? = ResourcesCompat.getFont(this, R.font.gotham_bold)
            tvMagical.typeface = myCustomFont

            val request = RewardCardDetailReq(cardNo, AppConstants.APP_VERSION,
                    AppConstants.APP_PLATFORM)
            if (UtilsDialog.isNetworkStatusAvialable(this@RewardDetailActivity)) {
                UtilsDialog.showProgressDialog(this@RewardDetailActivity, "")
                myAccountVM.getRewardCardDetailResponse(request)
            }

            val requestt = RewardTransactionReq(cardNo, AppConstants.APP_VERSION,
                    AppConstants.APP_PLATFORM)
            if (UtilsDialog.isNetworkStatusAvialable(this@RewardDetailActivity)) {
                UtilsDialog.showProgressDialog(this@RewardDetailActivity, "")
                myAccountVM.getRewardTransactionResponse(requestt)
            }
        }
        tvMagical.setOnClickListener {
            magical()
        }
        tvTransaction.setOnClickListener {
            transaction()

        }
        tvPoints = findViewById(R.id.tvPoint)
        tvPoints.setOnClickListener {
            tvNoReward.visibility = View.GONE
            toggleLayoutPointsRewards()
        }
        tvRewards = findViewById(R.id.tvRewards)
        tvRewards.setOnClickListener {
            tvNoPoints.visibility = View.GONE
            toggleLayoutPointsRewards()
        }
        tvTerms.setOnClickListener {
            Picasso.with(context).load(R.drawable.downarrow).into(ivDropDown)
            ivDropDown.setColorFilter(ContextCompat.getColor(context, R.color.grey_xlight));
            tvMagical.setTextColor(resources.getColor(R.color.grey_xlight))
            val myCustomFont: Typeface? = ResourcesCompat.getFont(this, R.font.gotham_book)
            tvMagical.typeface = myCustomFont
            Picasso.with(context).load(R.drawable.downarrow).into(ivDropDownB)
            ivDropDownB.setColorFilter(ContextCompat.getColor(context, R.color.grey_xlight));
            tvTransaction.setTextColor(resources.getColor(R.color.grey_xlight))
            val myCustomFontt: Typeface? = ResourcesCompat.getFont(this, R.font.gotham_book)
            tvTransaction.typeface = myCustomFontt
            if (!isterms) {
                isterms = true
                Picasso.with(context).load(R.drawable.uparrow).into(ivDropDownC)
                DrawableCompat.setTint(ivDropDownC.getDrawable(), ContextCompat.getColor(context,
                        R.color.white))
                tvTerms.setTextColor(resources.getColor(R.color.white))
                val myCustomFontt: Typeface? = ResourcesCompat.getFont(this, R.font.gotham_bold)
                tvTerms.typeface = myCustomFontt
                UtilsDialog.showProgressDialog(this@RewardDetailActivity, "")
                clmagical.visibility = View.GONE
                clTransaction.visibility = View.GONE
                webview.visibility = View.VISIBLE
                webview.setWebViewClient(MyWebViewClient())
                webview.getSettings().setJavaScriptEnabled(true)
                webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                webview.getSettings().setLoadsImagesAutomatically(true);
                webview.loadUrl(termsUrl)
            } else {
                isterms = false
                webview.visibility = View.GONE
                Picasso.with(context).load(R.drawable.downarrow).into(ivDropDownC)
                DrawableCompat.setTint(ivDropDownC.getDrawable(), ContextCompat.getColor(context,
                        R.color.grey_xlight))
                tvTerms.setTextColor(resources.getColor(R.color.grey_xlight))
            }
        }
    }

    private fun toggleLayoutPointsRewards() {
        if (isPoints) {
            isPoints = false
            clPoints.visibility = View.VISIBLE
            clRewards.visibility = View.GONE
            tvRewards.setTextColor(resources.getColor(R.color.grey_xlight))
            tvPoints.setTextColor(resources.getColor(R.color.marcus_red))
            if (pointsHistory.size == 0) {
                tvNoPoints.visibility = View.VISIBLE
                clFromTo.visibility = View.GONE
                clPoints.visibility =View.GONE
                //clFromTo.visibility = View.GONE
            }else{
                tvNoPoints.visibility = View.GONE
                clFromTo.visibility = View.VISIBLE
                clPoints.visibility =View.VISIBLE
            }

            tvFrom.setText("")
            tvTo.setText("")

        } else {
            isPoints = true
            clPoints.visibility = View.GONE
            clRewards.visibility = View.VISIBLE
            tvRewards.setTextColor(resources.getColor(R.color.marcus_red))
            tvPoints.setTextColor(resources.getColor(R.color.grey_xlight))
            tvFrom.setText("")
            tvTo.setText("")
            if (rewardHistory.size == 0) {
                tvNoReward.visibility = View.VISIBLE
                clFromTo.visibility = View.GONE
                clRewards.visibility = View.GONE
                //  clFromTo.visibility = View.GONE
            }else{
                tvNoReward.visibility = View.GONE
                clFromTo.visibility = View.VISIBLE
                clRewards.visibility = View.VISIBLE

            }
        }


        rvPoints.setLayoutManager(LinearLayoutManager(context, LinearLayoutManager.VERTICAL,
                false))
        val pointsAdapter = TransactionPointsAdapter(pointsHistory, context)
        rvPoints.setAdapter(pointsAdapter)

        val rewardsAdapter = TransactionRewardsAdapter(rewardHistory, context)
        rvRewards.setHasFixedSize(true)
        rvRewards.setLayoutManager(LinearLayoutManager(context, LinearLayoutManager.VERTICAL,
                false))
        rvRewards.setAdapter(rewardsAdapter)
    }


    fun transaction() {
        clmagical.visibility = View.GONE
        clTransaction.visibility = View.VISIBLE
        webview.visibility = View.GONE
        Picasso.with(context).load(R.drawable.downarrow).into(ivDropDown)
        ivDropDown.setColorFilter(ContextCompat.getColor(context, R.color.grey_xlight));
        tvMagical.setTextColor(resources.getColor(R.color.grey_xlight))
        val myCustomFontt: Typeface? = ResourcesCompat.getFont(this, R.font.gotham_book)
        tvMagical.typeface = myCustomFontt
        Picasso.with(context).load(R.drawable.downarrow).into(ivDropDownC)
        ivDropDown.setColorFilter(ContextCompat.getColor(context, R.color.grey_xlight));
        tvTerms.setTextColor(resources.getColor(R.color.grey_xlight))
        val myCustomFont: Typeface? = ResourcesCompat.getFont(this, R.font.gotham_book)
        tvTerms.typeface = myCustomFont

        if (!isTransaction) {
            isTransaction = true
            Picasso.with(context).load(R.drawable.uparrow).into(ivDropDownB)
            DrawableCompat.setTint(ivDropDownB.getDrawable(), ContextCompat.getColor(context,
                    R.color.white))
            tvTransaction.setTextColor(resources.getColor(R.color.white))
            val myCustomFont: Typeface? = ResourcesCompat.getFont(this, R.font.gotham_bold)
            tvTransaction.typeface = myCustomFont
          //  if (isPoints) {
                isPoints = false
                clPoints.visibility = View.VISIBLE
                clRewards.visibility = View.GONE
                tvRewards.setTextColor(resources.getColor(R.color.grey_xlight))
                tvPoints.setTextColor(resources.getColor(R.color.marcus_red))
          /*  } else {
                isPoints = true
                clPoints.visibility = View.GONE
                clRewards.visibility = View.VISIBLE
                tvRewards.setTextColor(resources.getColor(R.color.marcus_red))
                tvPoints.setTextColor(resources.getColor(R.color.grey_xlight))
            }*/

        } else {
            isTransaction = false
            clTransaction.visibility = View.GONE
            Picasso.with(context).load(R.drawable.downarrow).into(ivDropDownB)
            DrawableCompat.setTint(ivDropDownB.getDrawable(), ContextCompat.getColor(context,
                    R.color.grey_xlight))
            tvTransaction.setTextColor(resources.getColor(R.color.grey_xlight))
            val myCustomFont: Typeface? = ResourcesCompat.getFont(this, R.font.gotham_book)
            tvTransaction.typeface = myCustomFont
        }
    }

    fun magical() {
        clmagical.visibility = View.VISIBLE
        clTransaction.visibility = View.GONE
        webview.visibility = View.GONE
        Picasso.with(context).load(R.drawable.downarrow).into(ivDropDownB)
        ivDropDownB.setColorFilter(ContextCompat.getColor(context, R.color.grey_xlight));
        tvTransaction.setTextColor(resources.getColor(R.color.grey_xlight))
        val myCustomFont: Typeface? = ResourcesCompat.getFont(this, R.font.gotham_book)
        tvTransaction.typeface = myCustomFont
        Picasso.with(context).load(R.drawable.downarrow).into(ivDropDownC)
        ivDropDownC.setColorFilter(ContextCompat.getColor(context, R.color.grey_xlight));
        tvTerms.setTextColor(resources.getColor(R.color.grey_xlight))
        val myCustomFontt: Typeface? = ResourcesCompat.getFont(this, R.font.gotham_book)
        tvTerms.typeface = myCustomFontt
        if (isMagical) {
            isMagical = false
            clmagical.visibility = View.VISIBLE
            Picasso.with(context).load(R.drawable.uparrow).into(ivDropDown)
            ivDropDown.setColorFilter(ContextCompat.getColor(context, R.color.white));
            tvMagical.setTextColor(resources.getColor(R.color.white))
            val myCustomFontt: Typeface? = ResourcesCompat.getFont(this, R.font.gotham_bold)
            tvMagical.typeface = myCustomFontt
            val request = RewardCardDetailReq(cardNo, AppConstants.APP_VERSION,
                    AppConstants.APP_PLATFORM)
            if (UtilsDialog.isNetworkStatusAvialable(this@RewardDetailActivity)) {
                UtilsDialog.showProgressDialog(this@RewardDetailActivity,
                        "")
                myAccountVM.getRewardCardDetailResponse(request)
            }
        } else {
            isMagical = true
            Picasso.with(context).load(R.drawable.downarrow).into(ivDropDown)
            ivDropDown.setColorFilter(ContextCompat.getColor(context, R.color.grey_xlight));
            tvMagical.setTextColor(resources.getColor(R.color.grey_xlight))
            clmagical.visibility = View.GONE
            val myCustomFontt: Typeface? = ResourcesCompat.getFont(this, R.font.gotham_book)
            tvMagical.typeface = myCustomFontt
        }
    }

    private fun myAccountObserver() {
        myAccountVM = ViewModelProviders.of(this).get(MyAccountVM::class.java)
        myAccountVM.getRewardTransactionData().observe(this, rewardTransactionObs)
        myAccountVM.getRewardCardDetailData().observe(this, rewardCardDetailObs)
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

    private var rewardTransactionObs = object : Observer<RewardTransactionResp> {
        override fun onChanged(t: RewardTransactionResp?) {
            UtilsDialog.hideProgress()
            if (t!!.STATUS) {
                rewardHistory = t.DATA.rewards_history
                pointsHistory = t.DATA.points_history

                populateRewardData()
                populatePointsData()
            } else {
                alert(t!!.DATA.message,
                        getString(R.string.marcus_theatre_title)) {
                    positiveButton("OK") {
                        it.dismiss()
                    }
                }.show()
            }
        }
    }

    fun populatePointsData() {
        if (pointsHistory.size == 0) {
          //  clFromTo.visibility = View.GONE
            clPoints.visibility = View.GONE

        } else {
          //  clFromTo.visibility = View.VISIBLE
            clPoints.visibility = View.VISIBLE

            rvPoints.setHasFixedSize(true)
            rvPoints.setLayoutManager(LinearLayoutManager(context, LinearLayoutManager.VERTICAL,
                    false))
            val pointsAdapter = TransactionPointsAdapter(pointsHistory, context)
            rvPoints.setAdapter(pointsAdapter)
        }
    }

    fun populateRewardData() {
        if (rewardHistory.size == 0) {
           // clFromTo.visibility = View.GONE
            clRewards.visibility = View.GONE

        } else {
           // clFromTo.visibility = View.VISIBLE
           // clRewards.visibility = View.VISIBLE

            val rewardsAdapter = TransactionRewardsAdapter(rewardHistory, context)
            rvRewards.setHasFixedSize(true)
            rvRewards.setLayoutManager(LinearLayoutManager(context, LinearLayoutManager.VERTICAL,
                    false))
            rvRewards.setAdapter(rewardsAdapter)
        }
    }

    private var rewardCardDetailObs = object : Observer<RewardCardDetailResp> {
        override fun onChanged(t: RewardCardDetailResp?) {
            UtilsDialog.hideProgress()
            if (t!!.STATUS) {
                if (t.DATA.banner_url.isNotEmpty())
                    Picasso.with(context).load(t.DATA.banner_url).into(ivReward)
                termsUrl = t.DATA.terms_and_conditions_url
                tvPointBal.setText("POINTS BALANCE = " + t.DATA.point_balance.points)
                tvPointsCount.setText(t.DATA.point_balance.points_threshold)
                tvRewardbal.setText("REWARDS BALANCE = " + t.DATA.rewards_in_text)
                tvCardNo.setText("You are logged in with card " + t.DATA.card_number)
                val stext = t.DATA.notes
                val text = stext.replace("/n", "\n\n");
                tvPointDesc.setText(text)
            } else {
                alert(t!!.DATA.message, "Marcus Theatres") {

                    positiveButton("OK") { dialog ->
                        dialog.dismiss()
                    }
                }.show()
                //toast(t!!.DATA.message)
            }
        }
    }

    private inner class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            UtilsDialog.showProgressDialog(this@RewardDetailActivity, "")
            view.loadUrl(url)
            return true
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            UtilsDialog.hideProgress()
            Log.d("URL", "onPageFinished: : $url")
        }
    }

    fun selectDate(date: String) {
        val c = Calendar.getInstance()
        val mYear = c.get(Calendar.YEAR);
        val mMonth = c.get(Calendar.MONTH);
        val mDay = c.get(Calendar.DAY_OF_MONTH);
        val dpd = DatePickerDialog(this@RewardDetailActivity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            val sdf = SimpleDateFormat("yyyy/MM/dd")
            val strdt = dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year
            var strDate: Date? = null
            try {
                strDate = sdf.parse(strdt)
                if (System.currentTimeMillis() > strDate!!.time) {
                    if (date == "from") {
                        tvFrom.setText((monthOfYear + 1).toString() + "/" + dayOfMonth.toString() + "/" + year.toString())
                    } else {
                        tvTo.setText((monthOfYear + 1).toString() + "/" + dayOfMonth.toString() + "/" + year.toString())
                        val dateFormat = SimpleDateFormat("MM/dd/yyyy")
                        try {
                            val tvfrom = tvFrom.text.toString()
                            val tvto = tvTo.text.toString()
                            val fromDate: Date = dateFormat.parse(tvfrom)
                            val toDate: Date = dateFormat.parse(tvto)
                            System.out.println(fromDate.toString() + toDate.toString())
                            datefilter(fromDate, toDate)

                        } catch (e: ParseException) {
                            e.printStackTrace()
                        }
                    }
                } else {
                    alert( "Please select previous date",
                            getString(R.string.marcus_theatre_title)) {
                        positiveButton("OK") {
                            it.dismiss()
                        }
                    }.show()
                }
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }, mYear, mMonth, mDay)
        dpd.show()
        dpd.getDatePicker().setMaxDate(System.currentTimeMillis())

    }

    private fun datefilter(from: Date, to: Date) {
        if (!isPoints) {
            val Pointsnewhistory = CopyOnWriteArrayList<PointsHistory>()
            for (eachitem in pointsHistory) {

                val datelist = eachitem.transaction_date
                val store = eachitem.transaction_store
                val amnt = eachitem.points_earned
                val PointsHistory = PointsHistory(datelist, store, amnt)
                val formatter: DateFormat
                val date: Date
                formatter = SimpleDateFormat("MM/dd/yyyy")
                date = formatter.parse(datelist)
                Log.w("datelist", date.toString())
                if (date >= from && date <= to) {
                    Pointsnewhistory.add(PointsHistory)
                }


            }

             if (Pointsnewhistory.size == 0) {
                 tvNoPoints.visibility = View.VISIBLE
                 clFromTo.visibility = View.GONE
                 clPoints.visibility = View.GONE
             } else {
                 tvNoPoints.visibility = View.GONE
                 clFromTo.visibility = View.VISIBLE
                 clPoints.visibility = View.VISIBLE
                 val pointsAdapter = TransactionPointsAdapter(Pointsnewhistory, context)
            rvPoints.setHasFixedSize(true)
            rvPoints.setLayoutManager(LinearLayoutManager(context, LinearLayoutManager.VERTICAL,
                    false))
            rvPoints.adapter = pointsAdapter
            pointsAdapter.notifyDataSetChanged()}
        } else {
            val rewardsNewHistory = CopyOnWriteArrayList<RewardsHistory>()
            for (eachitem in rewardHistory) {

                val datelist = eachitem.transaction_date
                val store = eachitem.transaction_store
                val type = eachitem.transaction_type
                val amnt = eachitem.transaction_amount
                val rewardHistory = RewardsHistory(datelist, store, type, amnt)
                val formatter: DateFormat
                val date: Date
                formatter = SimpleDateFormat("MM/dd/yyyy")
                date = formatter.parse(datelist)
                Log.w("datelist", date.toString())
                if (date >= from && date <= to) {
                    rewardsNewHistory.add(rewardHistory)
                }
            }
            Log.w("filtersize", "" + rewardsNewHistory.size)
             if (rewardsNewHistory.size == 0) {
                 tvNoReward.visibility = View.VISIBLE
                 clFromTo.visibility = View.GONE
                 clRewards.visibility = View.GONE

             } else {
            tvNoReward.visibility = View.GONE
                 clFromTo.visibility = View.VISIBLE
                 clRewards.visibility = View.VISIBLE
                 val rewardsAdapter = TransactionRewardsAdapter(rewardsNewHistory, context)
            rvRewards.setHasFixedSize(true)
            rvRewards.setLayoutManager(LinearLayoutManager(context, LinearLayoutManager.VERTICAL,
                    false))
            rvRewards.adapter = rewardsAdapter
            rewardsAdapter.notifyDataSetChanged()
        }}
        // rewardHistory.sort({ d1, d2 -> d1.compareTo(d2) })

    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.animator.slide_from_left , R.animator.slide_to_right)    }
}
