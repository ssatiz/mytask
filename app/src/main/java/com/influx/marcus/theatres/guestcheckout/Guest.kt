package com.influx.marcus.theatres.guestcheckout

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.guest.GuestRegReq
import com.influx.marcus.theatres.api.ApiModels.guest.GuestRegResp
import com.influx.marcus.theatres.api.ApiModels.guest.Preference
import com.influx.marcus.theatres.commonview.UtilDialog
import com.influx.marcus.theatres.homepage.HomeActivity
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.CommonApi
import com.influx.marcus.theatres.utils.PhoneNumberTextWatcher
import com.influx.marcus.theatres.utils.UtilsDialog
import kotlinx.android.synthetic.main.activity_guest.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast

class Guest : AppCompatActivity()/*, CounterClass.CountdownTick*/ {


    private lateinit var context: Context
    private lateinit var guestVM: GuestVM
    private lateinit var tvTimer: TextView
    private lateinit var etMobilenumber: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this@Guest
        setContentView(R.layout.activity_guest)
        initViews()
        registerObservers()
    }

    private fun registerObservers() {
        guestVM = ViewModelProviders.of(this).get(GuestVM::class.java)
        guestVM.getGuestRegDetails().observe(this, guestRegObs)
        guestVM.getApiErrorDetails().observe(this, errorObs)
    }

    private var errorObs = object : Observer<String> {
        override fun onChanged(t: String?) {
            UtilsDialog.hideProgress()
            alert(getString(R.string.ohinternalservererror),
                    getString(R.string.marcus_theatre_title)) {
                positiveButton("OK") {
                    it.dismiss()
                }
            }.show()
        }
    }

    private var guestRegObs = object : Observer<GuestRegResp> {
        override fun onChanged(t: GuestRegResp?) {
            UtilsDialog.hideProgress()
            if (t!!.STATUS) {
                AppConstants.putString(AppConstants.KEY_GUEST_USER, t.DATA.userid, context)
                AppConstants.putString(AppConstants.KEY_USERID, t.DATA.userid, context)
                AppConstants.putString(AppConstants.KEY_EMAIL, t.DATA.email, context)
                AppConstants.putString(AppConstants.KEY_FIRSTNAME, t.DATA.firstname, context)

                if (AppConstants.getString(AppConstants.KEY_FROM, context) == "Navigation") {
                    val i = Intent(context, HomeActivity::class.java)
                    startActivity(i)
                    finish()
                    overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
                } else if (AppConstants.getString(AppConstants.KEY_FROM, context) == "Blockseat") {
                    CommonApi.blockThisSeat(context, AppConstants.getString(AppConstants.KEY_GUEST_USER, context))
                } else if (AppConstants.getString(AppConstants.KEY_FROM, context) == "UnreserveBlockseat") {
                    CommonApi.unReservedSeatLock(context, AppConstants.getString(AppConstants.KEY_GUEST_USER, context))
                }
            } else {
                alert(t.DATA.message,
                        getString(R.string.marcus_theatre_title)) {
                    positiveButton("OK") {
                        it.dismiss()
                    }
                }.show()
            }
        }
    }

    private fun initViews() {
        val ivBack = findViewById<ImageView>(R.id.ivBack)
        etMobilenumber = findViewById(R.id.etMobilenumber)
        etMobilenumber.addTextChangedListener(PhoneNumberTextWatcher(etMobilenumber))
        btnProceed.setOnClickListener {
            validateAndRegisterGuest()
        }
        ivBack.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                onBackPressed()
            }
        })
        tvTimer = findViewById(R.id.tvTime)
        //  CounterClass.setListener(this@Guest)
    }

    private fun validateAndRegisterGuest() {
        inputFName.isErrorEnabled = false
        inputlname.isErrorEnabled = false
        inputMail.isErrorEnabled = false
        inputmobile.isErrorEnabled = false

        if (editFName.text.toString().isBlank()) {
            inputFName.isErrorEnabled = true
            inputFName.error = "Please enter Firstname"
            return
        }

        if (etLName.text.toString().isBlank()) {
            inputlname.isErrorEnabled = true
            inputlname.error = "Please Enter Last Name"
            return
        }

        if (!UtilDialog.isValidEmail(etMail.text)) {
            inputMail.isErrorEnabled = true
            inputMail.error = "Please Enter valid Email"
            return
        }

        if (etMobilenumber.text.toString().isBlank()) {
            inputmobile.isErrorEnabled = true
            inputmobile.error = "Please Enter Mobile Number"
            return
        }
        val userPreference = Preference(
                AppConstants.getStringList(AppConstants.KEY_PREFEREDCINEMA, context),
                AppConstants.getStringList(AppConstants.KEY_PREFEREDGENRE, context),
                AppConstants.getStringList(AppConstants.KEY_PREFEREDLANGUAGE, context)
        )

        val guestReg = GuestRegReq(
                editFName.text.toString(),
                etLName.text.toString(),
                etMail.text.toString(),
                etMobilenumber.text.toString(),
                userPreference,
                AppConstants.getString(AppConstants.KEY_STATE_CODE, context),
                AppConstants.APP_VERSION,
                AppConstants.APP_PLATFORM
        )

        if (UtilsDialog.isNetworkStatusAvialable(context)) {
            UtilsDialog.showProgressDialog(context, "")
            guestVM.registerGuestUser(guestReg)
        }

    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.animator.slide_from_left , R.animator.slide_to_right)
    }
}