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
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.*
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.liststatecity.*
import com.influx.marcus.theatres.api.ApiModels.login.LOGINDATA
import com.influx.marcus.theatres.api.ApiModels.signup.Preference
import com.influx.marcus.theatres.api.ApiModels.signup.SignupReq
import com.influx.marcus.theatres.api.ApiModels.signup.SignupResp
import com.influx.marcus.theatres.api.ApiModels.upgradeloyalty.UpgradeLoyaltyReq
import com.influx.marcus.theatres.api.ApiModels.upgradeloyalty.UpgradeLoyaltyResp
import com.influx.marcus.theatres.commonview.UtilDialog
import com.influx.marcus.theatres.homepage.PrivacyPolicy
import com.influx.marcus.theatres.homepage.TermsandConditions
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.PhoneNumberTextWatcher
import com.influx.marcus.theatres.utils.UtilsDialog
import com.shagi.materialdatepicker.date.DatePickerFragmentDialog
import kotlinx.android.synthetic.main.activity_magical_signup.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import org.jetbrains.anko.toast
import java.util.*

class MagicalSignup : AppCompatActivity() {
    lateinit var context: Context
    lateinit var singupVM: SignupVm
    private var rewards: String = "1"
    var gender: String = "0"
    lateinit var year: String
    lateinit var month: String
    lateinit var date: String
    var stateCode: String = ""
    var city: String = ""
    var zipcode: String = ""
    var zipcodeValid = false
    var newsAndOffers = "1"
    lateinit var statelist: List<DATA>
    lateinit var cityList: List<CITYDATA>
    lateinit var ZipcodeList: List<PINCODEDATA>
    var stateArraylist = ArrayList<String>()
    var zipcodeList = ArrayList<String>()
    var cityArraylist = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_magical_signup)
        context = this@MagicalSignup
        signupObserver()
        initviews()
    }

    private fun initviews() {
        val ivBack = findViewById<ImageView>(R.id.ivBack)
        tvTerms.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG)
        tvPrivacy.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG)

        etPhNo.addTextChangedListener(PhoneNumberTextWatcher(etPhNo))

        val sidemenu = intent.extras.getString("sidemenu")
        if (sidemenu == "false") {
            tvJoin.setText(R.string.reg)
        }
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
                alert("Select your city",
                        getString(R.string.marcus_theatre_title)) {
                    positiveButton("OK") {
                        it.dismiss()
                    }
                }.show()
            } else {
                editZipcode.showDropDown()
            }
        }
        cbNews.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                newsAndOffers = "1"

            } else {
                newsAndOffers = "0"
            }

        }
        editState.setOnItemClickListener { parent, view, position, id ->
            editState.showDropDown()
            editCity.setText("")
            val selected = parent.getItemAtPosition(position) as String
            editState.setText(selected)
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
        }

        editCity.setOnItemClickListener { parent, view, position, id ->
            editCity.showDropDown()
            editZipcode.setText("")
            val selected = parent.getItemAtPosition(position) as String
            editCity.setText(selected)
            city = selected
            if (UtilsDialog.isNetworkStatusAvialable(context)) {
                UtilsDialog.showProgressDialog(context, "")
                editZipcode.setInputType(InputType.TYPE_CLASS_NUMBER);
                singupVM.getZipcodeResp(selected)
            }
        }

        editZipcode.setOnItemClickListener { parent, view, position, id ->
            editZipcode.showDropDown()
            val selected = parent.getItemAtPosition(position) as String
            zipcode = selected
            editZipcode.setText(selected)

        }
        btnSignup.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                signup()
            }
        })
        editDOB.setOnClickListener {
            selectDate()
        }

        rbGroup.setOnCheckedChangeListener { rbGroup, i ->
            var radioButton = findViewById(rbGroup.getCheckedRadioButtonId()) as RadioButton
            var i = rbGroup.indexOfChild(radioButton)
            if (i == 0) {
                gender = "1"
            } else if (i == 1) {
                gender = "2"
            } else if (i == 2) {
                gender = "3"
            }
        }

        ivBack.setOnClickListener {
            alert(getString(R.string.sure_leave_the_page), getString(R.string.marcus_theatre_title))
            {
                positiveButton("YES") { dialog ->
                    onBackPressed()

                }
                negativeButton("NO") { dialog ->
                    dialog.dismiss()
                }
            }.show().setCancelable(false)
        }
        prepopulateUserDataIfAvailable()
        /* doColorSpanForFirstString(getString(R.string.already), getString(R.string.sign_in), tvAlready)
         doColorSpanForSecondString(getString(R.string.already), getString(R.string.sign_in), tvAlready)
         doClickSpanForSignin(getString(R.string.already), getString(R.string.sign_in), tvAlready)*/
    }

    private fun signup() {
        inputFirstName.isErrorEnabled = false
        inputlastname.isErrorEnabled = false
        inputEmail.isErrorEnabled = false
        inputPasswd.isErrorEnabled = false

        if (editFirstName.text.isEmpty()) {
            inputFirstName.isErrorEnabled = true
            inputFirstName.error = "Please enter Firstname"
        } else if (etLastName.text.isEmpty()) {
            inputlastname.isErrorEnabled = true
            inputlastname.error = "Please enter Lastname"
        } else if (!UtilDialog.isValidEmail(etEmail.text)) {
            inputEmail.isErrorEnabled = true
            inputEmail.error = "Please enter valid email"
        } else if (editPasswrd.text.isEmpty()) {
            inputPasswd.isErrorEnabled = true
            inputPasswd.error = "Please enter password"
        } else if (editPasswrd.text.toString().length < 6) {
            inputPasswd.isErrorEnabled = true
            inputPasswd.error = "Password length should not be less than 6"
        } else if (etPhNo.text.isEmpty()) {
            inputPhNo.isErrorEnabled = true
            inputPhNo.error = "Please enter Mobile Number"
        } else if (gender.isEmpty()) {
            alert("Choose your gender",
                    getString(R.string.marcus_theatre_title)) {
                positiveButton("OK") {
                    it.dismiss()
                }
            }.show()
        } else if (editDOB.text.isEmpty()) {
            alert("Select your date",
                    getString(R.string.marcus_theatre_title)) {
                positiveButton("OK") {
                    it.dismiss()
                }
            }.show()
        } else if (editAddress.text.isEmpty()) {
            alert("Select your address",
                    getString(R.string.marcus_theatre_title)) {
                positiveButton("OK") {
                    it.dismiss()
                }
            }.show()
        } else if (editState.text.isEmpty()) {
            alert("Select your state",
                    getString(R.string.marcus_theatre_title)) {
                positiveButton("OK") {
                    it.dismiss()
                }
            }.show()
        } else if (editCity.text.isEmpty()) {
            alert("Select your city",
                    getString(R.string.marcus_theatre_title)) {
                positiveButton("OK") {
                    it.dismiss()
                }
            }.show()
        } else if (editZipcode.text.isEmpty()) {
            alert("Select your zipcode",
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
                    zipcodeValid = true
                }
            }
            if (zipcodeValid) {
                if (AppConstants.getString(AppConstants.KEY_USERID, this).isNotBlank() &&
                        AppConstants.getStringList(AppConstants.KEY_USERID, this).isNotEmpty()) {
                    val request = UpgradeLoyaltyReq(AppConstants.getString(AppConstants.KEY_USERID, this), editFirstName.text.toString(),
                            etLastName.text.toString(),
                            etEmail.text.toString(), editDOB.text.toString(),
                            etPhNo.text.toString(),
                            editAddress.text.toString(),
                            city, stateCode, zipcode,
                            AppConstants.APP_VERSION, AppConstants.APP_PLATFORM)
                    if (UtilsDialog.isNetworkStatusAvialable(context)) {
                        UtilsDialog.showProgressDialog(context, "")
                        singupVM.getUpgradeResp(request)
                    }

                } else {
                    val stateId = AppConstants.getString(AppConstants.KEY_STATE_CODE, context)
                    var preference = Preference(
                            AppConstants.getStringList(AppConstants.KEY_PREFEREDCINEMA, context),
                            AppConstants.getStringList(AppConstants.KEY_PREFEREDGENRE, context),
                            AppConstants.getStringList(AppConstants.KEY_PREFEREDLANGUAGE, context))

                    val request = SignupReq(editFirstName.text.toString(),
                            etLastName.text.toString(),
                            etEmail.text.toString(),
                            editPasswrd.text.toString(), etPhNo.text.toString(), rewards, newsAndOffers, gender,
                            editDOB.text.toString(), editAddress.text.toString(),
                            city, stateCode, zipcode, "", "", preference, stateId,
                            AppConstants.APP_VERSION, AppConstants.APP_PLATFORM)
                    if (UtilsDialog.isNetworkStatusAvialable(context)) {
                        UtilsDialog.showProgressDialog(context, "")
                        singupVM.getSignupResponse(request)
                    }

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

    }


    private fun signupObserver() {
        singupVM = ViewModelProviders.of(this).get(SignupVm::class.java)
        singupVM.getSignupRespData().observe(this, signupObs)
        singupVM.getApiErrorData().observe(this, errorObs)
        singupVM.getStateRespData().observe(this, stateObs)
        singupVM.getCityRespData().observe(this, cityObs)
        singupVM.getZipcodeRespData().observe(this, zipcodeObs)
        singupVM.getUpgradeRespData().observe(this, upgradeObs)

    }

    private var signupObs = object : Observer<SignupResp> {
        override fun onChanged(t: SignupResp?) {
            UtilsDialog.hideProgress()
            if (t!!.STATUS == true) {
                // toast(R.string.mmr_signup_success)
                val userId = t.DATA.userid
                AppConstants.putString(AppConstants.KEY_GUEST_USER, "", context)
                AppConstants.putString(AppConstants.KEY_USERID, userId, context)
                AppConstants.putString(AppConstants.KEY_EMAIL, etEmail.text.toString(), context)
                AppConstants.putString(AppConstants.KEY_FIRSTNAME, t.DATA.firstname, context)
                if (t.DATA.loyalty_no != null)
                    if (t.DATA.loyalty_no.size > 0) {
                        AppConstants.putString(AppConstants.KEY_LOYALTYCARDNO, t.DATA.loyalty_no.get(0).cart_no, context)
                        /* AppConstants.putString(AppConstants.KEY_MMRRESP, "Congratulations! You are upgraded to" +
                                 " our loyalty program. Your card number is: "+t.DATA.loyalty_no.get(0).cart_no, context)
 */
                    }
                val i = Intent(context, RegisterSuccess::class.java)
                i.putExtra("success_msg", t.DATA.loyalty_msg)
                i.putExtra("success_img", t.DATA.loyalty_img)
                startActivity(i)
                finish()
                overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
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

    private var stateObs = object : Observer<statelistResp> {
        override fun onChanged(t: statelistResp?) {
            stateArraylist.clear()
            UtilsDialog.hideProgress()
            if (t!!.STATUS == true) {
                statelist = t.DATA
                for (eachState in statelist) {
                    stateArraylist.add(eachState.state)
                }
                val adapter = ArrayAdapter<String>(context, R.layout.statelayout, R.id.tvState, stateArraylist)
                editState.threshold = 1//will start working from first character
                editState.setAdapter(adapter)
            }else{
            }
        }
    }
    private var upgradeObs = object : Observer<UpgradeLoyaltyResp> {
        override fun onChanged(t: UpgradeLoyaltyResp?) {
            UtilsDialog.hideProgress()
            if (t!!.STATUS == true) {
                if (t.DATA.loyalty_no != null)
                    if (t.DATA.loyalty_no.size > 0) {
                        AppConstants.putString(AppConstants.KEY_LOYALTYCARDNO, t.DATA.loyalty_no.get(0).cart_no, context)
                        /* AppConstants.putString(AppConstants.KEY_MMRRESP, "Congratulations! You are upgraded to" +
                                 " our loyalty program. Your card number is: "+t.DATA.loyalty_no.get(0).cart_no, context)
 */
                    }
                val i = Intent(context, RegisterSuccess::class.java)
                i.putExtra("success_msg", t.DATA.loyalty_msg)
                i.putExtra("success_img", t.DATA.loyalty_img)
                AppConstants.putObject(AppConstants.KEY_USERDATAOBJ, t.DATA, context)
                startActivity(i)
                finish()
                overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
            } else {
                alert(t!!.DATA.message, "Marcus Theatres")
                {
                    positiveButton("OK") { dialog ->
                        dialog.dismiss()
                    }
                }.show().setCancelable(false)
            }
        }
    }

    private fun prepopulateUserDataIfAvailable() {


        try {

            val userData = AppConstants.getObject(AppConstants.KEY_USERDATAOBJ,
                    context, LOGINDATA::class.java as Class<Any>) as LOGINDATA
            editFirstName.setText(userData.firstname)
            etLastName.setText(userData.lastname)
            etEmail.setText(userData.email)
            etPhNo.setText(userData.mobile)

//            if (userData.gender == "1") {
//                rbMale.isChecked = true
//                gender = "1"
//            }
//            if (userData.gender == "2") {
//                rbFemale.isChecked = true
//                gender = "2"
//            }
//            if (userData.gender == "3") {
//                rbOthers.isChecked = true
//                gender = "3"
//            }
//            editState.setText(userData.state)
//            editCity.setText(userData.city)
//            editAddress.setText(userData.address)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var cityObs = object : Observer<cityListResp> {
        override fun onChanged(t: cityListResp?) {
            UtilsDialog.hideProgress()
            cityArraylist.clear()
            if (t!!.STATUS == true) {
                cityList = t.DATA
                for (eachState in cityList) {
                    cityArraylist.add(eachState.place)
                }
                val adapter = ArrayAdapter<String>(context, R.layout.statelayout, R.id.tvState, cityArraylist)
                editCity.threshold = 1//will start working from first character
                editCity.setAdapter(adapter)
            }
        }
    }

    private var zipcodeObs = object : Observer<pincodeListResp> {
        override fun onChanged(t: pincodeListResp?) {
            UtilsDialog.hideProgress()
            zipcodeList.clear()
            if (t!!.STATUS == true) {
                ZipcodeList = t.DATA
                for (eachState in ZipcodeList) {
                    zipcodeList.add(eachState.zip)
                }
                val adapter = ArrayAdapter<String>(context, R.layout.statelayout, R.id.tvState, zipcodeList)
                editZipcode.threshold = 1//will start working from first character
                editZipcode.setAdapter(adapter)
            }
        }
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

        // spanText.setSpan(MyClickableSpan(context),
        //  firstString.toString().length + 1, totalString.length, 0)
        txtSpan.text = spanText
        txtSpan.movementMethod = LinkMovementMethod.getInstance()
    }

    /*class MyClickableSpan(var context: Context) : ClickableSpan() {
        override fun onClick(widget: View?) {
            val i = Intent(context, LoginScreen::class.java)
            context.startActivity(i)
        }

        override fun updateDrawState(ds: TextPaint?) {
            ds?.color = (ContextCompat.getColor(context, R.color.marcus_red))
            ds?.isUnderlineText = true
        }

    }*/
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


    private val onDismissListener = AutoCompleteTextView.OnDismissListener {
        if (editState.text.length == 0) {
            cityArraylist.clear()
            zipcodeList.clear()
            editCity.setText("")
            editZipcode.setText("")

            val adapter = ArrayAdapter<String>(this@MagicalSignup, R.layout.statelayout, R.id.tvState, cityArraylist)
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
            val adapter = ArrayAdapter<String>(this@MagicalSignup, R.layout.statelayout, R.id.tvState, zipcodeList)
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
        if (editZipcode.text.length > 0) {
            val Zipcode = editZipcode.text.toString()
            for (eachzipcode in zipcodeList) {
                if (eachzipcode.equals(Zipcode)) {
                    zipcode = eachzipcode.toString()
                }
            }
        }
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.animator.slide_from_left , R.animator.slide_to_right)
    }

}
