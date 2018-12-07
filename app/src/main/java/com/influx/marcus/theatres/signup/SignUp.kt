package com.influx.marcus.theatres.signup

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Paint

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.*
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.liststatecity.*
import com.influx.marcus.theatres.api.ApiModels.login.LOGINDATA
import com.influx.marcus.theatres.api.ApiModels.signup.Preference
import com.influx.marcus.theatres.api.ApiModels.signup.SIGNUPDATA
import com.influx.marcus.theatres.api.ApiModels.signup.SignupReq
import com.influx.marcus.theatres.api.ApiModels.signup.SignupResp
import com.influx.marcus.theatres.commonview.UtilDialog
import com.influx.marcus.theatres.homepage.HomeActivity
import com.influx.marcus.theatres.homepage.PrivacyPolicy
import com.influx.marcus.theatres.homepage.TermsandConditions
import com.influx.marcus.theatres.login.LoginScreen
import com.influx.marcus.theatres.showtime.ShowtimeActivity
import com.influx.marcus.theatres.theatres.TheatreShowTimeActivity
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.CommonApi
import com.influx.marcus.theatres.utils.PhoneNumberTextWatcher
import com.influx.marcus.theatres.utils.UtilsDialog
import com.shagi.materialdatepicker.date.DatePickerFragmentDialog
import kotlinx.android.synthetic.main.activity_signup.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.toast
import java.util.*
import kotlin.collections.ArrayList

class SignUp : AppCompatActivity()/*, CounterClass.CountdownTick*/ {

    lateinit var context: Context
    lateinit var singupVM: SignupVm
    private lateinit var tvTimer: TextView
    private var rewards: String = ""
    var gender: String = "0"
    lateinit var year: String
    lateinit var month: String
    var date = ""
    var stateCode = ""
    var city = ""
    var zipcode = ""
    var fname = ""
    var newsAndOffers = "1"
    lateinit var statelist: List<DATA>
    lateinit var cityList: List<CITYDATA>
    lateinit var ZipcodeList: List<PINCODEDATA>
    var stateArraylist = ArrayList<String>()
    var zipcodeList = ArrayList<String>()
    var cityArraylist = ArrayList<String>()
    lateinit var etPhNo: EditText
    var zipcodeValid = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        context = this@SignUp
        signupObserver()
        initviews()
    }

    private fun initviews() {
        val ivBack = findViewById<ImageView>(R.id.ivBack)
        etPhNo = findViewById<EditText>(R.id.etPhNo)
        etPhNo.addTextChangedListener(PhoneNumberTextWatcher(etPhNo))
        editCity.setInputType(InputType.TYPE_NULL);
        editZipcode.setInputType(InputType.TYPE_NULL);
        tvTerms.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG)
        tvPrivacy.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG)

        if (UtilsDialog.isNetworkStatusAvialable(context)) {
            UtilsDialog.showProgressDialog(context, "")
            stateArraylist.clear()
            cityArraylist.clear()
            zipcodeList.clear()
            singupVM.getstateResp()
        }

        editState.setOnClickListener {
            editState.showDropDown()
        }

        editState.setOnDismissListener(onDismissListener)
        editCity.setOnDismissListener(onCityDismissListener)
        editZipcode.setOnDismissListener(onZipCodeDismissListener)

        editCity.setOnClickListener {
            if (stateCode.isEmpty()) {
                alert("Select your state",
                        getString(R.string.marcus_theatre_title)) {
                    positiveButton("OK") {
                        it.dismiss()
                    }
                }.show()
            } else {
                editCity.showDropDown()
            }
        }
        editZipcode.setOnClickListener {
            if (city.isEmpty()) {
                alert( "Select your city",
                        getString(R.string.marcus_theatre_title)) {
                    positiveButton("OK") {
                        it.dismiss()
                    }
                }.show()
            } else {
                editZipcode.showDropDown()
            }
        }
        tvTerms.setOnClickListener {
            val i = Intent(context, TermsandConditions::class.java)
            startActivity(i)
            overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
        }

        tvPrivacy.setOnClickListener {
            val i = Intent(context, PrivacyPolicy::class.java)
            startActivity(i)
            overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
        }
        cbRegister.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                rewards = "1"
                clMagicalLayout.visibility = View.VISIBLE

            } else {
                rewards = "0"
                clMagicalLayout.visibility = View.GONE
            }

        }

        cbNews.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                newsAndOffers = "1"

            } else {
                newsAndOffers = "0"
            }

        }

        if (AppConstants.getBoolean(AppConstants.KEY_NEWUSER, context) == true) {
            editFirstName.setText(AppConstants.getString(AppConstants.KEY_FIRSTNAME, context))
            etLastName.setText(AppConstants.getString(AppConstants.KEY_LASTNAME, context))
            tvAlready.visibility = View.GONE
            if (AppConstants.getString(AppConstants.KEY_EMAIL, context).isNotEmpty()) {
                etEmail.setText(AppConstants.getString(AppConstants.KEY_EMAIL, context))
                etEmail.setFocusable(false);
                etEmail.setFocusableInTouchMode(false)
                etEmail.setClickable(false)

            } else {
                etEmail.setFocusable(true);
                etEmail.setFocusableInTouchMode(true); // user touches widget on phone with touch screen
                etEmail.setClickable(true);
            }

        } else {

        }
        if (AppConstants.getBoolean(AppConstants.KEY_BOOKINGFLOW, context)) {
            cbRegister.visibility = View.GONE
            tvRegister.visibility = View.GONE
        } else {
            cbRegister.visibility = View.VISIBLE
            tvRegister.visibility = View.VISIBLE
            /*editAddress.setText(intent.extras.getString("address"))
            gender(Integer.parseInt(intent.extras.getString("gender")))
            editCity.setText(intent.extras.getString("city"))
            editState.setText(intent.extras.getString("state"))*/

        }
        btnSignup.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                signup()
            }
        })
        editDOB.setOnClickListener {
            selectDate()
        }

        rbGroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { rbGroup, i ->
            var radioButton = findViewById(rbGroup.getCheckedRadioButtonId()) as RadioButton
            var i = rbGroup.indexOfChild(radioButton)
            gender(i)
        })

        ivBack.setOnClickListener {
            onBackPressed()
        }

        doColorSpanForFirstString(getString(R.string.already), getString(R.string.sign_in), tvAlready)
        doColorSpanForSecondString(getString(R.string.already), getString(R.string.sign_in), tvAlready)
        doClickSpanForSignin(getString(R.string.already), getString(R.string.sign_in), tvAlready)
        tvTimer = findViewById(R.id.tvTime)
        // CounterClass.setListener(this@SignUp)
        editState.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            editState.showDropDown()
            val selected = parent.getItemAtPosition(position) as String
            editState.setText(selected)
            editCity.setText("")
            editZipcode.setText("")
            for (eachstateid in statelist) {
                if (eachstateid.state.equals(selected)) {
                    stateCode = eachstateid.scode
                    if (UtilsDialog.isNetworkStatusAvialable(context)) {
                        UtilsDialog.showProgressDialog(context, "")
                        editCity.setInputType(InputType.TYPE_CLASS_TEXT);
                        singupVM.getCityResp(stateCode)
                    }
                }
            }
        })
        editCity.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            editCity.showDropDown()
            editZipcode.setText("")
            val selected = parent.getItemAtPosition(position) as String
            editCity.setText(selected)
            for (eachcityid in cityArraylist) {
                if (eachcityid.equals(selected)) {
                    city = selected
                    if (UtilsDialog.isNetworkStatusAvialable(context)) {
                        UtilsDialog.showProgressDialog(context, "")
                        editZipcode.setInputType(InputType.TYPE_CLASS_NUMBER);
                        singupVM.getZipcodeResp(selected)
                    }
                }
            }

        })
        editZipcode.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            editZipcode.showDropDown()
            val selected = parent.getItemAtPosition(position) as String
            zipcode = selected
            editZipcode.setText(selected)

        })
    }

    fun gender(i: Int) {
        if (i == 0) {
            gender = "1"
        } else if (i == 1) {
            gender = "2"
        } else if (i == 2) {
            gender = "3"
        }
    }

    private val onDismissListener = AutoCompleteTextView.OnDismissListener {
        if (editState.text.length == 0) {
            cityArraylist.clear()
            zipcodeList.clear()
            editCity.setText("")
            editZipcode.setText("")

            val adapter = ArrayAdapter<String>(this@SignUp, R.layout.statelayout, R.id.tvState, cityArraylist)
            editCity.threshold = 1//will start working from first character
            editCity.setAdapter(adapter)
            editCity.setInputType(InputType.TYPE_NULL)
        } else {
            val state = editState.text.toString()
            for (eachstateid in statelist) {
                if (eachstateid.state.equals(state)) {
                    stateCode = eachstateid.scode
                    if (UtilsDialog.isNetworkStatusAvialable(context)) {
                        UtilsDialog.showProgressDialog(context, "")
                        editCity.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                        singupVM.getCityResp(stateCode)
                    }
                }
            }
        }

    }
    private val onCityDismissListener = AutoCompleteTextView.OnDismissListener {
        if (editCity.text.length == 0) {
            zipcodeList.clear()
            editZipcode.setText("")
            val adapter = ArrayAdapter<String>(this@SignUp, R.layout.statelayout, R.id.tvState, zipcodeList)
            editZipcode.threshold = 1//will start working from first character
            editZipcode.setAdapter(adapter)
            editZipcode.setInputType(InputType.TYPE_NULL)
        } else {
            val City = editCity.text.toString()
            for (eachCity in cityArraylist) {
                if (eachCity.equals(City)) {
                    city = eachCity.toString()
                    if (UtilsDialog.isNetworkStatusAvialable(context)) {
                        UtilsDialog.showProgressDialog(context, "")
                        editZipcode.setInputType(InputType.TYPE_CLASS_NUMBER);
                        singupVM.getZipcodeResp(city)
                    }
                }
            }
        }
    }
    private val onZipCodeDismissListener = AutoCompleteTextView.OnDismissListener {
         if (editZipcode.text.length > 4) {
             val Zipcode = editZipcode.text.toString()
             for (eachzipcode in zipcodeList) {
                 if (eachzipcode.equals(Zipcode)) {
                     zipcode = eachzipcode.toString()
                 }
             }
         }
    }

    private fun signup() {
        inputFirstName.isErrorEnabled = false
        inputlastname.isErrorEnabled = false
        inputEmail.isErrorEnabled = false
        inputPasswd.isErrorEnabled = false


        if (editFirstName.text.isEmpty()) {
            fname = editFirstName.text.toString()
            fname = fname.replace(" ", "")
            inputFirstName.isErrorEnabled = true
            inputFirstName.error = "Please enter Firstname"
            alert("Please enter Firstname",
                    getString(R.string.marcus_theatre_title)) {
                positiveButton("OK") {
                    it.dismiss()
                }
            }.show()

        } else if (etLastName.text.isEmpty()) {
            inputlastname.isErrorEnabled = true
            inputlastname.error = "Please enter Lastname"
            alert("Please enter Lastname",
                    getString(R.string.marcus_theatre_title)) {
                positiveButton("OK") {
                    it.dismiss()
                }
            }.show()

        } else if (!UtilDialog.isValidEmail(etEmail.text)) {
            inputEmail.isErrorEnabled = true
            inputEmail.error = "Please enter valid email"
            alert("Please enter valid email",
                    getString(R.string.marcus_theatre_title)) {
                positiveButton("OK") {
                    it.dismiss()
                }
            }.show()

        } else if (editPasswrd.text.isEmpty()) {
            inputPasswd.isErrorEnabled = true
            inputPasswd.error = "Please enter password"
            alert("Please enter password",
                    getString(R.string.marcus_theatre_title)) {
                positiveButton("OK") {
                    it.dismiss()
                }
            }.show()

        } else if (editPasswrd.text.toString().length < 6) {
            inputPasswd.isErrorEnabled = true
            inputPasswd.error = "Password length should not be less than 6"
            alert("Password length should not be less than 6",
                    getString(R.string.marcus_theatre_title)) {
                positiveButton("OK") {
                    it.dismiss()
                }
            }.show()
        } else if (etPhNo.text.isEmpty()) {
            inputPhNo.isErrorEnabled = true
            inputPhNo.error = "Please enter Mobile Number"
            alert("Please enter Mobile Number",
                    getString(R.string.marcus_theatre_title)) {
                positiveButton("OK") {
                    it.dismiss()
                }
            }.show()
        } else if (cbTerms.isChecked == false) {
            alert("Please accept Terms and Conditions",
                    getString(R.string.marcus_theatre_title)) {
                positiveButton("OK") {
                    it.dismiss()
                }
            }.show()
        } else if (rewards == "1") {
            if (gender.isEmpty()) {
                alert("Please Choose your gender",
                        getString(R.string.marcus_theatre_title)) {
                    positiveButton("OK") {
                        it.dismiss()
                    }
                }.show()
            } else if (editDOB.text.isEmpty()) {
                alert("Please Select your date",
                        getString(R.string.marcus_theatre_title)) {
                    positiveButton("OK") {
                        it.dismiss()
                    }
                }.show()
            } else if (editAddress.text.isEmpty()) {
                alert("Please Select your address",
                        getString(R.string.marcus_theatre_title)) {
                    positiveButton("OK") {
                        it.dismiss()
                    }
                }.show()
            } else if (editState.text.isEmpty()) {
                alert("Please Select your state",
                        getString(R.string.marcus_theatre_title)) {
                    positiveButton("OK") {
                        it.dismiss()
                    }
                }.show()
            } else if (editCity.text.isEmpty()) {
                alert("Please Select your city",
                        getString(R.string.marcus_theatre_title)) {
                    positiveButton("OK") {
                        it.dismiss()
                    }
                }.show()
            } else if (editZipcode.text.isEmpty()) {
                alert("Please Select your zipcode",
                        getString(R.string.marcus_theatre_title)) {
                    positiveButton("OK") {
                        it.dismiss()
                    }
                }.show()
            } else if (cbTerms.isChecked == false) {
                alert("Please accept Terms and Conditions",
                        getString(R.string.marcus_theatre_title)) {
                    positiveButton("OK") {
                        it.dismiss()
                    }
                }.show()
            } else {
                val Zipcode = editZipcode.text.toString()
                for (eachzipcode in zipcodeList) {
                    if (eachzipcode.equals(Zipcode)) {
                        zipcode = eachzipcode.toString()
                        zipcodeValid = true
                    }
                }
                if (zipcodeValid) {
                    val stateId = AppConstants.getString(AppConstants.KEY_STATE_CODE, this@SignUp)
                    var preference = Preference(
                            AppConstants.getStringList(AppConstants.KEY_PREFEREDCINEMA, this@SignUp),
                            AppConstants.getStringList(AppConstants.KEY_PREFEREDGENRE, this@SignUp),
                            AppConstants.getStringList(AppConstants.KEY_PREFEREDLANGUAGE, this@SignUp))
                    Log.i("prefered cinema", AppConstants.getStringList(AppConstants.KEY_PREFEREDCINEMA, context).toString())
                    Log.i("prefered cinema", AppConstants.getStringList(AppConstants.KEY_PREFEREDGENRE, context).toString())
                    Log.i("prefered cinema", AppConstants.getStringList(AppConstants.KEY_PREFEREDLANGUAGE, context).toString())
                    fname = editFirstName.text.toString()
                    fname = fname.replace(" ", "")
                    val request = SignupReq(fname,
                            etLastName.text.toString().trim(),
                            etEmail.text.toString(),
                            editPasswrd.text.toString(), etPhNo.text.toString(), rewards, newsAndOffers, gender,
                            editDOB.text.toString(), editAddress.text.toString(),
                            city, stateCode, zipcode, AppConstants.getString(AppConstants.KEY_SOCIAL, context),
                            AppConstants.getString(AppConstants.KEY_SOCIALID, context), preference, stateId,
                            AppConstants.APP_VERSION, AppConstants.APP_PLATFORM)
                    if (UtilsDialog.isNetworkStatusAvialable(context)) {
                        UtilsDialog.showProgressDialog(context, "")
                        singupVM.getSignupResponse(request)
                    }
                } else {
                    alert("please select zipcode",
                            getString(R.string.marcus_theatre_title)) {
                        positiveButton("OK") {
                            it.dismiss()
                        }
                    }.show()
                }
            }
        } else if (cbTerms.isChecked == false) {
            alert("Please accept Terms and Conditions",
                    getString(R.string.marcus_theatre_title)) {
                positiveButton("OK") {
                    it.dismiss()
                }
            }.show()
        } else {
            val stateId = AppConstants.getString(AppConstants.KEY_STATE_CODE, this@SignUp)
            var preference = Preference(
                    AppConstants.getStringList(AppConstants.KEY_PREFEREDCINEMA, this@SignUp),
                    AppConstants.getStringList(AppConstants.KEY_PREFEREDGENRE, this@SignUp),
                    AppConstants.getStringList(AppConstants.KEY_PREFEREDLANGUAGE, this@SignUp))
            Log.i("prefered cinema", AppConstants.getStringList(AppConstants.KEY_PREFEREDCINEMA, context).toString())
            Log.i("prefered cinema", AppConstants.getStringList(AppConstants.KEY_PREFEREDGENRE, context).toString())
            Log.i("prefered cinema", AppConstants.getStringList(AppConstants.KEY_PREFEREDLANGUAGE, context).toString())
            fname = editFirstName.text.toString()
            fname = fname.replace(" ", "")
            val request = SignupReq(fname,
                    etLastName.text.toString(),
                    etEmail.text.toString(),
                    editPasswrd.text.toString(), etPhNo.text.toString(), rewards, newsAndOffers, gender,
                    editDOB.text.toString(), editAddress.text.toString(),
                    city, stateCode, zipcode, AppConstants.getString(AppConstants.KEY_SOCIAL, context),
                    AppConstants.getString(AppConstants.KEY_SOCIALID, context), preference, stateId,
                    AppConstants.APP_VERSION, AppConstants.APP_PLATFORM)
            if (UtilsDialog.isNetworkStatusAvialable(context)) {
                UtilsDialog.showProgressDialog(context, "")
                singupVM.getSignupResponse(request)
            }
        }
    }


    private fun signupObserver() {
        singupVM = ViewModelProviders.of(this).get(SignupVm::class.java)
        singupVM.getSignupRespData().observe(this, signupObs)
        singupVM.getApiErrorData().observe(this, errorObs)
        singupVM.getStateRespData().observe(this, stateObs)
        singupVM.getCityRespData().observe(this, cityObs)
        singupVM.getZipcodeRespData().observe(this, zipcodeObs)
    }

    private var signupObs = object : Observer<SignupResp> {
        override fun onChanged(t: SignupResp?) {
            UtilsDialog.hideProgress()
            if (t!!.STATUS == true) {
                val userId = t.DATA.userid
                AppConstants.putString(AppConstants.KEY_USERID, userId, context)
                AppConstants.putString(AppConstants.KEY_EMAIL, t.DATA.email, context)
                AppConstants.putString(AppConstants.KEY_GUEST_USER, "", context)
                AppConstants.putString(AppConstants.KEY_FIRSTNAME, t.DATA.firstname, context)

                val loginResp: LOGINDATA = getLoginDataFromSignupResp(t.DATA)
                AppConstants.putObject(AppConstants.KEY_USERDATAOBJ, loginResp, context)
                if (t.DATA.preference.cinemas != null) {
                    AppConstants.cinemaList.clear()
                    AppConstants.cinemaList.addAll(t.DATA.preference.cinemas)
                    AppConstants.putStringList(AppConstants.KEY_PREFEREDCINEMA,
                            AppConstants.cinemaList, context)
                }

                //todo verify from backend how signup response will come

//                AppConstants.localCinemadata.clear()
//                val cinemaNames: ArrayList<String> = ArrayList()
//                if (t.DATA.preference.cinemas_name != null) {
//                    cinemaNames.addAll(t.DATA.preference.cinemas_name)
//                }
//                cinemaNames.forEachIndexed { index, cinemaName ->
//                    AppConstants.localCinemadata.add(
//                            CinemaDetail(cinemaName,
//                                    AppConstants.cinemaList.get(index))
//                    )
//                }
//
//                val localCinemaDetail = LocalCinemaData(AppConstants.localCinemadata)
//                AppConstants.putObject(AppConstants.KEY_PREFEREDCINEMALISTOBJ,
//                        localCinemaDetail, context)


                if (t.DATA.preference.languages != null) {
                    AppConstants.languageList.clear()
                    AppConstants.languageList.addAll(t.DATA.preference.languages)
                    AppConstants.putStringList(AppConstants.KEY_PREFEREDLANGUAGE,
                            AppConstants.languageList, context)
                }


                if (t.DATA.preference.genres != null) {
                    AppConstants.genreList.clear()
                    AppConstants.genreList.addAll(t.DATA.preference.genres)
                    AppConstants.putStringList(AppConstants.KEY_PREFEREDGENRE,
                            AppConstants.genreList, context)
                }


                if (t.DATA.loyalty_no != null)
                    if (t.DATA.loyalty_no.size > 0) {
                        AppConstants.putString(AppConstants.KEY_LOYALTYCARDNO, t.DATA.loyalty_no.get(0).cart_no, context)
                    }

                if (AppConstants.getString(AppConstants.KEY_FROM, context) == "Navigation") {
                    val i = Intent(context, HomeActivity::class.java)
                    startActivity(i)
                    overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
                } else if (AppConstants.getString(AppConstants.KEY_FROM, context) == "Blockseat") {
                    CommonApi.blockThisSeat(context, userId)
                } else if (AppConstants.getString(AppConstants.KEY_FROM, context) == "UnreserveBlockseat") {
                    CommonApi.unReservedSeatLock(context, userId)
                }


                if (t.DATA.loyalty_no != null)
                    if (t.DATA.loyalty_no.size > 0) {
                        AppConstants.putString(AppConstants.KEY_LOYALTYCARDNO, t.DATA.loyalty_no.get(0).cart_no, context)
                    }
                if (AppConstants.getString(AppConstants.KEY_FROM, context) == "Navigation") {
                    val i = Intent(context, HomeActivity::class.java)
                    startActivity(i)
                    finish()
                    overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
                } else if (AppConstants.getString(AppConstants.KEY_FROM, context) == "Blockseat") {
                    CommonApi.blockThisSeat(context, userId)
                } else if (AppConstants.getString(AppConstants.KEY_FROM, context) == "UnreserveBlockseat") {
                    CommonApi.unReservedSeatLock(context, userId)
                }
                alert("Registration Successful",
                        getString(R.string.marcus_theatre_title)) {
                    positiveButton("OK") {
                        it.dismiss()
                    }
                }.show()
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

    private fun getLoginDataFromSignupResp(data: SIGNUPDATA): LOGINDATA {
        var signupPreference = data.preference
        var loginDATA = LOGINDATA(
                data.userid,
                data.firstname,
                data.lastname,
                data.email,
                data.mobile,
                data.gender,
                data.address,
                data.city,
                data.state,
                data.country,
                data.loyalty_no,
                data.state_code,
                null,
                false,
                ""
        )
        return loginDATA
    }

    private var stateObs = object : Observer<statelistResp> {
        override fun onChanged(t: statelistResp?) {
            stateArraylist.clear()
            UtilsDialog.hideProgress()
            if (t!!.STATUS == true) {
                statelist = t.DATA
                for (eachState in statelist) {
                    stateArraylist.add(eachState.state)
                }
                val adapter = ArrayAdapter<String>(this@SignUp, R.layout.statelayout, R.id.tvState, stateArraylist)
                editState.threshold = 1//will start working from first character
                editState.setAdapter(adapter)
            }
        }
    }

    private var cityObs = object : Observer<cityListResp> {
        override fun onChanged(t: cityListResp?) {
            cityArraylist.clear()
            UtilsDialog.hideProgress()
            if (t!!.STATUS == true) {
                cityList = t.DATA
                for (eachCity in cityList) {
                    cityArraylist.add(eachCity.place)
                }
                val adapter = ArrayAdapter<String>(this@SignUp, R.layout.statelayout, R.id.tvState, cityArraylist)
                editCity.threshold = 1//will start working from first character
                editCity.setAdapter(adapter)
            }
        }
    }

    private var zipcodeObs = object : Observer<pincodeListResp> {
        override fun onChanged(t: pincodeListResp?) {
            zipcodeList.clear()
            UtilsDialog.hideProgress()
            if (t!!.STATUS == true) {
                ZipcodeList = t.DATA
                for (eachState in ZipcodeList) {
                    zipcodeList.add(eachState.zip)
                }
                val adapter = ArrayAdapter<String>(this@SignUp, R.layout.statelayout, R.id.tvState, zipcodeList)
                editZipcode.threshold = 1//will start working from first character
                editZipcode.setAdapter(adapter)
            }
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

    private fun doColorSpanForFirstString(firstString: String?,
                                          lastString: String, txtSpan: TextView) {

        val changeString = firstString ?: ""

        val totalString = changeString + " " + lastString
        val spanText = SpannableString(totalString)
        spanText.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.white)),
                0, changeString.length, 0)

        txtSpan.text = spanText
    }

    private fun doColorSpanForSecondString(firstString: String,
                                           lastString: String?, txtSpan: TextView) {
        val changeString = lastString ?: ""
        val totalString = firstString + " " + changeString
        val spanText = SpannableString(totalString)
        spanText.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.marcus_red)), firstString.toString()
                .length + 1, totalString.length, 0)
        txtSpan.text = spanText
    }

    private fun doClickSpanForSignin(firstString: String,
                                     lastString: String?, txtSpan: TextView) {
        val changeString = lastString ?: ""
        val totalString = firstString + " " + changeString
        val spanText = SpannableString(totalString)

        spanText.setSpan(MyClickableSpan(this@SignUp),
                firstString.toString().length + 1, totalString.length, 0)
        txtSpan.text = spanText
        txtSpan.movementMethod = LinkMovementMethod.getInstance()
    }

    class MyClickableSpan(var context: Context) : ClickableSpan() {
        override fun onClick(widget: View?) {
            val i = Intent(context, LoginScreen::class.java)
            context.startActivity(i)
        }

        override fun updateDrawState(ds: TextPaint?) {
            ds?.color = (ContextCompat.getColor(context, R.color.marcus_red))
            ds?.isUnderlineText = true
        }

    }

    /*
        override fun onTick(secondsLeft: Long) {
            val minutes = secondsLeft / 60
            val seconds = secondsLeft % 60
            tvTimer.text = CounterClass.getFormatedTime()
            LogUtils.d("Timertick", " $minutes - $seconds - $secondsLeft")
            if (secondsLeft < 1) {
                // show timeout
                tvTimer.text = "00:00 LEFT"
                tvTimer.clearAnimation()
                if (CounterClass.getInstance() != null) CounterClass.getInstance().cancel()
                if (!isFinishing) {
                    alert("Your session has timed out", "Marcus Theatres")
                    {
                        positiveButton("OK") { dialog ->
                            val fromdata = AppConstants.getString(AppConstants.KEY_SHOWTIME, context)
                            when (fromdata) {
                                "theatre_showtime" -> {
                                    val intentShowtime = Intent(context, TheatreShowTimeActivity::class.java)
                                    intentShowtime.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intentShowtime)
                                    finish()
                                    dialog.dismiss()
                                }
                                "movie_showtime" -> {
                                    val intentShowtime = Intent(context, ShowtimeActivity::class.java)
                                    intentShowtime.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intentShowtime)
                                    finish()
                                    dialog.dismiss()
                                }
                                else -> {
                                    val intentShowtime = Intent(context, ShowtimeActivity::class.java)
                                    intentShowtime.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intentShowtime)
                                    finish()
                                    dialog.dismiss()
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
    */
    fun selectDate() {
        val c = Calendar.getInstance()
        val mYear = c.get(Calendar.YEAR);
        val mMonth = c.get(Calendar.MONTH);
        val mDay = c.get(Calendar.DAY_OF_MONTH);
        val dialog = DatePickerFragmentDialog.newInstance({ view, year, monthOfYear, dayOfMonth ->
            val isAgeMoreThenTwelve = UtilsDialog.getAge(year, monthOfYear, dayOfMonth)
            if (isAgeMoreThenTwelve) {
                // do nothing allow user to select the date
                editDOB.setText((monthOfYear + 1).toString() + "-" + dayOfMonth.toString() + "-" + year)
            } else {
                alert("You must be 13 years of age or above to register", "Marcus Theatres") {
                    okButton {
                        it.dismiss()
                    }
                }.show()
            }
        }, mYear, mMonth, mDay)

        dialog.show(supportFragmentManager, "tag")
        dialog.setMaxDate(System.currentTimeMillis())

    }


    override fun onBackPressed() {
        if (AppConstants.getString(AppConstants.KEY_FROM, this@SignUp)
                        .equals("Navigation", ignoreCase = true)) {
            super.onBackPressed()
            overridePendingTransition(R.animator.slide_from_left , R.animator.slide_to_right)

        } else {
            alert("Do you want to cancel your booking", "Marcus Theatres")
            {
                positiveButton("YES") { dialog ->
                    val fromdata = AppConstants.getString(AppConstants.KEY_SHOWTIME, context)
                    when (fromdata) {
                        "theatre_showtime" -> {
                            val intentShowtime = Intent(context, TheatreShowTimeActivity::class.java)
                            intentShowtime.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intentShowtime)
                            finish()
                            overridePendingTransition(R.animator.slide_from_left , R.animator.slide_to_right)
                            dialog.dismiss()
                        }
                        "movie_showtime" -> {
                            val intentShowtime = Intent(context, ShowtimeActivity::class.java)
                            intentShowtime.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intentShowtime)
                            finish()
                            overridePendingTransition(R.animator.slide_from_left , R.animator.slide_to_right)
                            dialog.dismiss()
                        }
                        else -> {
                            val intentShowtime = Intent(context, ShowtimeActivity::class.java)
                            intentShowtime.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intentShowtime)
                            finish()
                            overridePendingTransition(R.animator.slide_from_left , R.animator.slide_to_right)
                            dialog.dismiss()
                        }
                    }
                }
                negativeButton("NO") { dialog ->
                    dialog.dismiss()
                }
            }.show().setCancelable(false)
        }
    }
}
