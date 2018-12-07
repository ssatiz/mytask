package com.influx.marcus.theatres.specials

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.TextView
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.specials.PromotionType
import com.influx.marcus.theatres.api.ApiModels.specials.Special
import com.influx.marcus.theatres.api.ApiModels.specials.SpecialResp
import com.influx.marcus.theatres.forgotpassword.SpecialsVM
import com.influx.marcus.theatres.homepage.HomeActivity
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.LogUtils
import com.influx.marcus.theatres.utils.UtilsDialog
import kotlinx.android.synthetic.main.activity_specials.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.toast

class SpecialsActivity : AppCompatActivity() {


    lateinit var specialsVM: SpecialsVM
    private var specialResp: SpecialResp? = null
    lateinit var tvTimer: TextView
    lateinit var promotionTypes: List<PromotionType>
    lateinit var specialType: ArrayList<String>
    var filteredSpecialType: ArrayList<Special> = ArrayList<Special>()
    lateinit var nonSpecialType: ArrayList<Special>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_specials)
        initViews()
        specialObserver()
    }

    private fun initViews() {

        try {
            specialResp = AppConstants.getObject(AppConstants.KEY_SPECIAL,
                    applicationContext, SpecialResp::class.java as Class<Any>) as? SpecialResp

            if (specialResp != null) {
                specialObs.onChanged(specialResp)
            } else {
                if (UtilsDialog.isNetworkStatusAvialable(this@SpecialsActivity)) {
                    UtilsDialog.showProgressDialog(this@SpecialsActivity, "")
                    specialsVM.getSpecialsResponse()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        ivFilter.setOnClickListener {
            val i = Intent(this@SpecialsActivity, FilterActivity::class.java)
            startActivity(i)
            finish()
            overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)


        }
        ivBackToolbar.setOnClickListener {
            val i = Intent(this@SpecialsActivity, HomeActivity::class.java)
            startActivity(i)
            finish()
            overridePendingTransition(R.animator.slide_from_left , R.animator.slide_to_right)

        }

        rvSpecial.setHasFixedSize(true)
        rvSpecial.setLayoutManager(LinearLayoutManager(this@SpecialsActivity, LinearLayoutManager.VERTICAL, false))

    }

    private fun specialObserver() {
        specialsVM = ViewModelProviders.of(this).get(SpecialsVM::class.java)
        specialsVM.getSpecialsData().observe(this, specialObs)
        specialsVM.getApiErrorData().observe(this, errorObs)
    }

    private var specialObs = object : Observer<SpecialResp> {
        override fun onChanged(t: SpecialResp?) {


            if (t!!.STATUS == true) {
                filteredSpecialType.clear()
                promotionTypes = t!!.DATA.promotion_types
                nonSpecialType = t!!.DATA.specials
                AppConstants.putObject(AppConstants.KEY_PROMOTIONS_DATA, t!!.DATA, this@SpecialsActivity)

                LogUtils.i("nonSpecial", nonSpecialType.toString())
                LogUtils.i("KEY_PROMOTIONSsss", AppConstants.getStringList(AppConstants.KEY_PROMOTIONS,
                        this@SpecialsActivity).toString())
                specialType = AppConstants.getStringList(AppConstants.KEY_PROMOTIONS, this@SpecialsActivity)
                if (AppConstants.getStringList(AppConstants.KEY_PROMOTIONS, this@SpecialsActivity).isNotEmpty()
                        && (AppConstants.getStringList(AppConstants.KEY_PROMOTIONS, this@SpecialsActivity)).size > 0) {
                    for (eachitem in nonSpecialType) {
                        if (eachitem.promotion_type in specialType) {
                            filteredSpecialType.add(eachitem)
                            LogUtils.i("filtered special", filteredSpecialType.toString())
                        } else {
                            //do nothing
                        }

                    }
                    UtilsDialog.hideProgress()
                    if (filteredSpecialType.size > 0) {
                        LogUtils.i("filtered special", filteredSpecialType.toString())
                        val mAdapter = SpecialsAdapter(filteredSpecialType, this@SpecialsActivity,giftCardListener)
                        rvSpecial.adapter = mAdapter
                    } else {
                        tvNodata.visibility = View.VISIBLE
                        rvSpecial.visibility = View.GONE
                    }
                }
                else {
                    val mAdapter = SpecialsAdapter(nonSpecialType, this@SpecialsActivity,giftCardListener)
                    rvSpecial.adapter = mAdapter
                    UtilsDialog.hideProgress()
                }

            } else {
                //toast(t.DATA.message)
            }
        }
    }
    private var giftCardListener = object : SpecialsAdapter.SpecialsAdapterListener {
        override fun getDetails(position: Int, title: String, image: String, url: String) {
            val intent = Intent(this@SpecialsActivity, SpecialWebActivity::class.java)
            intent.putExtra("weburl", url)
            intent.putExtra("title", title)
            intent.putExtra("image", image)
            startActivity(intent)
            overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)

        }
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

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.animator.slide_from_left , R.animator.slide_to_right)
    }

}
