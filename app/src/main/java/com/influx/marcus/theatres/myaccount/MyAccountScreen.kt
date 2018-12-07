package com.influx.marcus.theatres.myaccount

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.myaccount.*
import com.influx.marcus.theatres.api.ApiModels.myaccountupdate.UpdateAccountReq
import com.influx.marcus.theatres.api.ApiModels.myaccountupdate.UpdateResponse
import com.influx.marcus.theatres.commonview.UtilDialog
import com.influx.marcus.theatres.commonview.WrapContentViewPagerMyAccount
import com.influx.marcus.theatres.customCamera.PickerBuilder
import com.influx.marcus.theatres.homepage.ViewPagerAdapter
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.LogUtils
import com.influx.marcus.theatres.utils.PhoneNumberTextWatcher
import com.influx.marcus.theatres.utils.UtilsDialog
import com.shagi.materialdatepicker.date.DatePickerFragmentDialog
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_my_account_screen.*
import org.jetbrains.anko.*
import java.io.ByteArrayOutputStream
import java.util.*


class MyAccountScreen : AppCompatActivity() {

    private var viewpagerAdapter: ViewPagerAdapter? = null
    var gender: String = "0"
    lateinit var myAccountVM: MyAccountVM
    private var myAccountResp: MyAccountResp? = null
    var removeFacebook: String = "0"
    var removeGoogle: String = "0"
    var newsLetter: String = "0"
    var marcusOnFacebook: String = "0"
    lateinit var bookingHistory: BookingHistory
    lateinit var preference: Preference
    lateinit var moviesYoumayLike: List<MovieYoulikeItem>
    lateinit var year: String
    lateinit var month: String
    lateinit var date: String
    lateinit var mProgress: ProgressDialog
    private val TAG = "Marcus"
    var strImage: String? = ""
    lateinit var loyaltyNo: LoyaltyNo
    lateinit var giftCards: GiftCards
    private val PERMISSION_REQUEST_CODE_1 = 2
    private val PERMISSION_REQUEST_CODE_2 = 3
    private val PERMISSION_REQUEST_CODE_3 = 4
    private lateinit var vpMyAccount: WrapContentViewPagerMyAccount
    val context: Context = this@MyAccountScreen
    var etBoolean: Boolean = true
    lateinit var editPhno: EditText
    var isFromPrefMofiy = false
    lateinit var profile_image: CircleImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_account_screen)
//        try {
//
//        } catch (e: Exception) {
//            AppConstants.putObject(AppConstants.KEY_SEATDATA, "", context)
//            AppConstants.putObject(AppConstants.KEY_BLOCKRESP, "", context)
//            AppConstants.putObject(AppConstants.KEY_BLOCKREQ, "", context)
//            AppConstants.putObject(AppConstants.KEY_UNRESERVE_BLOCKREQ, "", context)
//            Log.i("myaccountcrash", e.toString())
//            val i = Intent(this@MyAccountScreen, MyAccountScreen::class.java)
//            startActivity(i)
//            finish()
//        }
//
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        profile_image = findViewById(R.id.profile_image)
        myAccountObserver()
        mProgress = ProgressDialog(this)
        val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        im!!.hideSoftInputFromWindow(editDOB.getWindowToken(), 0)

        // default api call patch
        //todo to remove patch api call
        val request = MyAccountReq(AppConstants.getString(AppConstants.KEY_USERID,
                this@MyAccountScreen), AppConstants.APP_VERSION,
                AppConstants.APP_PLATFORM)
        if (UtilsDialog.isNetworkStatusAvialable(this@MyAccountScreen)) {
            UtilsDialog.showProgressDialog(this@MyAccountScreen, "")
            myAccountVM.getMyAccountResponse(request)
        }

        if (intent.hasExtra("modify")) {
            isFromPrefMofiy = intent.getBooleanExtra("modify", false)
        }

        vpMyAccount = findViewById(R.id.profTabviewpager)
        tvProfile.setOnClickListener {
            if (tvProfile.text == "Edit Profile") {
                Proftabs.visibility = View.GONE
                vpMyAccount.visibility = View.GONE
                ivEdit.visibility = View.VISIBLE
                val request = MyAccountReq(AppConstants.getString(AppConstants.KEY_USERID,
                        this@MyAccountScreen), AppConstants.APP_VERSION,
                        AppConstants.APP_PLATFORM)
                if (UtilsDialog.isNetworkStatusAvialable(this@MyAccountScreen)) {
                    UtilsDialog.showProgressDialog(this@MyAccountScreen, "")
                    myAccountVM.getMyAccountResponse(request)
                }
                tvProfile.setText("Close")
                profilelayout.visibility = View.VISIBLE
                ivSetting.setImageResource(R.drawable.ic_close_black_24dp)
                ivUpdate.visibility = View.VISIBLE
            } else {
                ivEdit.visibility = View.GONE
                Proftabs.visibility = View.VISIBLE
                vpMyAccount.visibility = View.VISIBLE
                tvProfile.setText("Edit Profile")
                profilelayout.visibility = View.GONE
                ivSetting.setImageResource(R.drawable.ic_settings_black_24dp)
                ivSetting.visibility = View.VISIBLE
                ivUpdate.visibility = View.GONE
            }
        }
        ivUpdate.setOnClickListener {
            validate("update")
        }
        cbNewsletter.setOnClickListener {
            var bv = cbNewsletter.isChecked
            if (bv == false) {
                newsLetter = "0"
            } else {
                newsLetter = "1"
            }
        }
        ivBackic.setOnClickListener {
            onBackPressed()

        }
        editDOB.setOnClickListener {

            if (etBoolean == true) {
                selectDate(year, month, date)
                etBoolean = false
            } else {
                val date = editDOB.text.toString().trim({ it <= ' ' }).split("-".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                val strmonth = date[0]
                val strday = date[1]
                val stryear = date[2]
                selectDate(stryear, strmonth, strday)
            }
        }

        unlinkGoogle.setOnClickListener {
            validate("google")
        }

        unlinkFacebook.setOnClickListener {
            validate("facebook")
        }
        ivEdit.setOnClickListener {
            selectImagenew()
        }

        rbGroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { rbGroup, i ->
            var radioButton = findViewById(rbGroup.getCheckedRadioButtonId()) as RadioButton
            var i = rbGroup.indexOfChild(radioButton)
            if (i == 0) {
                /*  rbMale.setButtonDrawable(R.drawable.radio_select)
              rbFemale.setButtonDrawable(R.drawable.radio_unselect)
              rbOthers.setButtonDrawable(R.drawable.radio_unselect)*/
                gender = "1"
            } else if (i == 1) {
                /* rbFemale.setButtonDrawable(R.drawable.radio_select)
             rbMale.setButtonDrawable(R.drawable.radio_unselect)
             rbOthers.setButtonDrawable(R.drawable.radio_unselect)*/
                gender = "2"
            } else if (i == 2) {
                /* rbOthers.setButtonDrawable(R.drawable.radio_select)
             rbMale.setButtonDrawable(R.drawable.radio_unselect)
             rbFemale.setButtonDrawable(R.drawable.radio_unselect)*/
                gender = "3"
            }
        })
    }

    fun validate(type: String) {
        inputFName.isErrorEnabled = false
        inputLName.isErrorEnabled = false
        inputEMail.isErrorEnabled = false
        inputPasswd.isErrorEnabled = false
        if (editFName.text.isEmpty()) {
            inputFName.isErrorEnabled = true
            inputFName.error = "Please enter First Name"
        } else if (editLName.text.isEmpty()) {
            inputLName.isErrorEnabled = true
            inputLName.error = "Please enter Last Name"
        } else if (!UtilDialog.isValidEmail(editEmail.text)) {
            inputEMail.isErrorEnabled = true
            inputEMail.error = "Please enter valid email"
        } else if (editPasswrd.text.isEmpty()) {
            inputPasswd.isErrorEnabled = true
            inputPasswd.error = "Please enter password"
        } else if (editPasswrd.text.length < 6) {
            inputPasswd.isErrorEnabled = true
            inputPasswd.error = "Password must contain atleast 6 letters"
        } else {
            if (type == "update") {
                val request = UpdateAccountReq(AppConstants.getString(AppConstants.KEY_USERID,
                        this@MyAccountScreen)/*"1083"*/, editFName.text.toString(),
                        editLName.text.toString(), editEmail.text.toString(), editPhno.text.toString(),
                        strImage.toString(), gender, editDOB.text.toString(), editPasswrd.text.toString(), removeFacebook,
                        removeGoogle, newsLetter, marcusOnFacebook,
                        AppConstants.APP_VERSION, AppConstants.APP_PLATFORM)
                if (UtilsDialog.isNetworkStatusAvialable(this@MyAccountScreen)) {
                    UtilsDialog.showProgressDialog(this@MyAccountScreen, "")
                    myAccountVM.getUpdateAccountResponse(request)
                }

            } else if (type == "google") {
                if (ivFb.visibility != View.VISIBLE) {
                    tvConnectedAccount.visibility = View.GONE
                }
                ivGoogle.visibility = View.GONE
                tvConnectedAc.visibility = View.GONE
                unlinkGoogle.visibility = View.GONE
                removeGoogle = "1"
            } else if (type == "facebook") {
                if (ivGoogle.visibility != View.VISIBLE) {
                    tvConnectedAccount.visibility = View.GONE
                }
                ivFb.visibility = View.GONE
                tvConnectedAcc.visibility = View.GONE
                unlinkFacebook.visibility = View.GONE
                removeFacebook = "1"
            }

        }
    }

    private fun myAccountObserver() {
        myAccountVM = ViewModelProviders.of(this).get(MyAccountVM::class.java)
        myAccountVM.getMyAccountData().observe(this, myAccountObs)
        myAccountVM.getUpdateAccountData().observe(this, updateAccountObs)
        myAccountVM.getApiErrorData().observe(this, errorObs)
    }


    private var myAccountObs = object : Observer<MyAccountResp> {
        override fun onChanged(t: MyAccountResp?) {
            UtilsDialog.hideProgress()

            editPhno = findViewById(R.id.editPhno)
            editPhno.addTextChangedListener(PhoneNumberTextWatcher(editPhno))
            if (t!!.STATUS == true) {
                // toast(t.STATUS.toString())
                editFName.setText(t.DATA.firstname)
                editLName.setText(t.DATA.lastname)
                editEmail.setText(t.DATA.email)
                editPhno.setText(t.DATA.mobile)
                editDOB.setText(t.DATA.dob)
                editPasswrd.setText(t.DATA.decrpt_pwd)
                preference = t.DATA.preference
                if (t.DATA.booking_history != null) {
                    bookingHistory = t.DATA.booking_history
                }
                if (t.DATA.movie_youlike != null) {
                    moviesYoumayLike = t.DATA.movie_youlike
                }
                loyaltyNo = t.DATA.loyalty_no
                giftCards = t.DATA.gift_cards
                vpMyAccount.offscreenPageLimit = 6
                setupViewPager(vpMyAccount)
                Proftabs.setupWithViewPager(vpMyAccount)
                vpMyAccount.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                    override fun onPageScrollStateChanged(state: Int) {

                    }

                    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

                    }

                    override fun onPageSelected(position: Int) {
                        vpMyAccount.reMeasureCurrentPage(position)
                    }
                })
                try {
                    if (t.DATA.img_url.isNotBlank()) {
                        Picasso.with(this@MyAccountScreen)
                                .load(t.DATA.img_url)
                                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                .networkPolicy(NetworkPolicy.NO_CACHE)
                                .into(profile_image)
                        Log.i("profileimage", t.DATA.img_url)
                    }
                } catch (e: java.lang.Exception) {
                    Log.i("failprofileimage", t.DATA.img_url)
                }
                val tokens = StringTokenizer(t.DATA.dob, "-")
                date = tokens.nextToken() // this will contain "Fruit"
                month = tokens.nextToken()
                year = tokens.nextToken()

                if (t.DATA.facebook == "1") {
                    tvConnectedAccount.visibility = View.VISIBLE
                    ivFb.visibility = View.VISIBLE
                    tvConnectedAcc.visibility = View.VISIBLE
                    unlinkFacebook.visibility = View.VISIBLE
                    // cbAddMarcus.visibility = View.VISIBLE
                }
                if (t.DATA.google == "1") {
                    tvConnectedAccount.visibility = View.VISIBLE
                    ivGoogle.visibility = View.VISIBLE
                    tvConnectedAc.visibility = View.VISIBLE
                    unlinkGoogle.visibility = View.VISIBLE
                    // cbAddMarcus.visibility = View.VISIBLE
                }

                if (t.DATA.newsletter == "1") {
                    newsLetter = t.DATA.newsletter
                    cbNewsletter.isChecked = true
                }

                if (t.DATA.marcusOnfacebook == "1") {
                    marcusOnFacebook = t.DATA.marcusOnfacebook
                    //   cbAddMarcus.isChecked = true
                }


                if (t.DATA.gender == "1") {
                    rbMale.isChecked = true
                    gender = "1"
                }
                if (t.DATA.gender == "2") {
                    rbFemale.isChecked = true
                    gender = "2"

                }
                if (t.DATA.gender == "3") {
                    rbOthers.isChecked = true
                    gender = "3"
                }
            } else {
                alert(t.DATA.message,
                        getString(R.string.marcus_theatre_title)) {
                    positiveButton("OK") {
                        it.dismiss()
                    }
                }.show()
                // toast(t.DATA.message)
                tvConnectedAccount.visibility = View.VISIBLE
                ivFb.visibility = View.VISIBLE
                tvConnectedAcc.visibility = View.VISIBLE
                unlinkFacebook.visibility = View.VISIBLE
                //  cbAddMarcus.visibility = View.VISIBLE

            }
            if (AppConstants.getString(AppConstants.KEY_FROM_SIDEMENU, context) != null &&
                    AppConstants.getString(AppConstants.KEY_FROM_SIDEMENU, context) == "magical") {
                AppConstants.putString(AppConstants.KEY_FROM_SIDEMENU, "", context)
                val pos = 1
                vpMyAccount.postDelayed(Runnable { vpMyAccount.setCurrentItem(pos) }, 50)
            } else if (AppConstants.getString(AppConstants.KEY_FROM_SIDEMENU, context) != null &&
                    AppConstants.getString(AppConstants.KEY_FROM_SIDEMENU, context) == "gift") {
                AppConstants.putString(AppConstants.KEY_FROM_SIDEMENU, "", context)
                val pos = 2
                vpMyAccount.postDelayed(Runnable { vpMyAccount.setCurrentItem(pos) }, 50)
            } else if (AppConstants.getString(AppConstants.KEY_FROM_SIDEMENU, context) != null &&
                    AppConstants.getString(AppConstants.KEY_FROM_SIDEMENU, context) == "preference") {
                AppConstants.putString(AppConstants.KEY_FROM_SIDEMENU, "", context)
                val pos = 4
                vpMyAccount.postDelayed(Runnable { vpMyAccount.setCurrentItem(pos) }, 50)
            } else {
                //do nothing
            }
        }
    }

    private var errorObs = object : Observer<String> {
        override fun onChanged(t: String?) {
            UtilsDialog.hideProgress()
            LogUtils.i("response", t!!)
            alert(getString(R.string.ohinternalservererror),
                    getString(R.string.marcus_theatre_title)) {
                positiveButton("OK") {
                    it.dismiss()
                }
            }.show()

        }
    }
    private var updateAccountObs = object : Observer<UpdateResponse> {
        override fun onChanged(t: UpdateResponse?) {
            UtilsDialog.hideProgress()
            alert("Updated Successfully",
                    getString(R.string.marcus_theatre_title)) {
                positiveButton("OK") {
                    it.dismiss()
                }
            }.show()

        }
    }

    private fun setupViewPager(viewPager: ViewPager) {
        doAsync {

            viewpagerAdapter = ViewPagerAdapter(supportFragmentManager)
            viewpagerAdapter?.addFragment(BookingHistoryFragment.newInstance(bookingHistory), "My Tickets")
            viewpagerAdapter?.addFragment(Rewards.newInstance(loyaltyNo), "Rewards")
            viewpagerAdapter?.addFragment(GiftCardsFragment.newInstance(giftCards), "Gift Cards")
            viewpagerAdapter?.addFragment(MoviesLikeFragment.newInstance(moviesYoumayLike), "Movies You May Like")
            viewpagerAdapter?.addFragment(Accts_Preference.newInstance(preference), "Preferences")
            //viewpagerAdapter?.addFragment(OffersFragment(), "Offers For You")
            uiThread {
                viewPager.adapter = viewpagerAdapter
                clMyaccount.visibility=View.VISIBLE

            }
        }

    }

    private fun setupViewPager(viewPager: ViewPager, defaultItem: Int) {
        doAsync {
            viewpagerAdapter = ViewPagerAdapter(supportFragmentManager)
            viewpagerAdapter?.addFragment(BookingHistoryFragment.newInstance(bookingHistory), "My Tickets")
            viewpagerAdapter?.addFragment(Rewards.newInstance(loyaltyNo), "Rewards")
            viewpagerAdapter?.addFragment(GiftCardsFragment.newInstance(giftCards), "Gift Cards")
            viewpagerAdapter?.addFragment(MoviesLikeFragment.newInstance(moviesYoumayLike), "Movies You May Like")
            viewpagerAdapter?.addFragment(Accts_Preference.newInstance(preference), "Preferences")
            //viewpagerAdapter?.addFragment(OffersFragment(), "Offers For You")
            viewPager.adapter = viewpagerAdapter
            uiThread {
                viewPager.setCurrentItem(defaultItem)
            }
        }
    }

    fun selectDate(year: String, month: String, date: String) {
        val c = Calendar.getInstance()
        val mYear = year.toInt()
        val mMonth = month.toInt() - 1
        val mDay = date.toInt()

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


    private fun getScaledBitmap(picturePath: String, width: Int, height: Int): Bitmap {
        val sizeOptions = BitmapFactory.Options()
        sizeOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(picturePath, sizeOptions)

        val inSampleSize = calculateInSampleSize(sizeOptions, width, height)

        sizeOptions.inJustDecodeBounds = false
        sizeOptions.inSampleSize = inSampleSize

        return BitmapFactory.decodeFile(picturePath, sizeOptions)
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            // Calculate ratios of height and width to requested height and
            // width
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }

        return inSampleSize
    }


    private fun selectImagenew() {

        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(this@MyAccountScreen)
        builder.setTitle("Add Photo!")
        builder.setItems(options) { dialog, item ->
            if (options[item] == "Take Photo") {
                val result = getCameraPermission()
                if (result == 1) {
                    Log.d(TAG, "camera: from main dialogd")
                    PickerBuilder(this@MyAccountScreen, PickerBuilder.SELECT_FROM_CAMERA)
                            .setOnImageReceivedListener(object : PickerBuilder.onImageReceivedListener {
                                override fun onImageReceived(imageUri: Uri) {
                                    // Toast.makeText(LookbookActivity.this, "Got image - " + imageUri, Toast.LENGTH_LONG).show();

                                    Handler().postDelayed({
                                        try {
                                            //
                                            profile_image.setImageBitmap(BitmapFactory.decodeFile(imageUri.encodedPath))

                                            filepath(BitmapFactory.decodeFile(imageUri.encodedPath))

                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }, 1000)

                                }
                            })
                            .setImageName("Marcuscinemas")
                            .setImageFolderName("marcusfolder")
                            .withTimeStamp(false)
                            .setCropScreenColor(Color.CYAN)
                            .setOnPermissionRefusedListener(object : PickerBuilder.onPermissionRefusedListener {
                                override fun onPermissionRefused() {
                                    Log.d(TAG, "onPermissionRefused: refused camera")
                                }
                            })
                            .start()
                } else {
                    alert("Permission Required",
                            getString(R.string.marcus_theatre_title)) {
                        positiveButton("OK") {
                            it.dismiss()
                        }
                    }.show()
                }
            } else if (options[item] == "Choose from Gallery") {
                val result = getGalleryPermission()
                if (result == 1) {
                    PickerBuilder(this@MyAccountScreen, PickerBuilder.SELECT_FROM_GALLERY)
                            .setOnImageReceivedListener(object : PickerBuilder.onImageReceivedListener {
                                override fun onImageReceived(imageUri: Uri) {
                                    Handler().postDelayed({
                                        try {
                                            profile_image.setImageBitmap(BitmapFactory.decodeFile(imageUri.encodedPath))
                                            filepath(BitmapFactory.decodeFile(imageUri.encodedPath))

                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }, 1000)
                                }
                            })
                            .setImageName("Marcuscinemas")
                            .setImageFolderName("marcusfolder")
                            .setCropScreenColor(Color.CYAN)
                            .setOnPermissionRefusedListener(object : PickerBuilder.onPermissionRefusedListener {
                                override fun onPermissionRefused() {

                                }
                            })
                            .start()
                }

            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()

    }

    private fun getGalleryPermission(): Int {

        if (ContextCompat.checkSelfPermission(this@MyAccountScreen, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@MyAccountScreen, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE_3)
            return 0
        } else {
            return 1
        }

    }


    /**
     * This method will get camera permission for
     */
    private fun getCameraPermission(): Int {

        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this@MyAccountScreen, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@MyAccountScreen, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE_1)
                return 0
            } else {
                // permission is already granted
                if (ContextCompat.checkSelfPermission(this@MyAccountScreen, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this@MyAccountScreen, arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CODE_2)
                    return 0
                } else {
                    return 1
                }
            }
        } else {
            // do nothing
            Log.d(TAG, "getCameraPermission: lower then marshmallow")
            return 1
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE_1 -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this@MyAccountScreen, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this@MyAccountScreen, arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CODE_2)
                } else {
                    Log.d(TAG, "onRequestPermissionsResult: code 1, camera already granted")
                    startCameraToCapture()
                }
            } else {
                android.support.v7.app.AlertDialog.Builder(this@MyAccountScreen)
                        .setTitle("Permission Alert")
                        .setMessage("WRITE_EXTERNAL_STORAGE")
                        .setPositiveButton("Retry") { dialog, which ->
                            // continue with delete
                            // requestPermission1();
                        }
                        .setNegativeButton("close") { dialog, which ->
                            // do nothing
                            finish()
                        }
                        .setIcon(R.mipmap.ic_launcher).show()
            }
            PERMISSION_REQUEST_CODE_2 -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult: code 2")
                startCameraToCapture()
            } else {
                android.support.v7.app.AlertDialog.Builder(this@MyAccountScreen)
                        .setTitle("Permission Alert")
                        .setMessage("CAMERA PERMISSION")
                        .setPositiveButton("Retry") { dialog, which ->
                            // continue with delete
                            //requestPermission2();
                        }
                        .setNegativeButton("close") { dialog, which ->
                            // do nothing
                            finish()
                        }
                        .setIcon(R.mipmap.ic_launcher).show()
            }
            PERMISSION_REQUEST_CODE_3 -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult: code 3")
                startGalleryToSelectImage()
            } else {
                android.support.v7.app.AlertDialog.Builder(this@MyAccountScreen)
                        .setTitle("Permission Alert")
                        .setMessage("PHONE PERMISSION")
                        .setPositiveButton("Retry") { dialog, which ->
                            // continue with delete
                            //  requestPermission3();
                        }
                        .setNegativeButton("close") { dialog, which ->
                            // do nothing
                            finish()
                        }
                        .setIcon(R.mipmap.ic_launcher).show()
            }
        }
    }

    /**
     * This method will select image from gallery
     */
    private fun startGalleryToSelectImage() {
        PickerBuilder(this@MyAccountScreen, PickerBuilder.SELECT_FROM_GALLERY)
                .setOnImageReceivedListener(object : PickerBuilder.onImageReceivedListener {
                    override fun onImageReceived(imageUri: Uri) {


                        Handler().postDelayed({
                            try {

                                profile_image.setImageBitmap(BitmapFactory.decodeFile(imageUri.encodedPath))
                                filepath(BitmapFactory.decodeFile(imageUri.encodedPath))

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }, 1000)
                    }
                })
                .setImageName("Marcuscinemas")
                .setImageFolderName("marcusfolder")
                .setCropScreenColor(Color.CYAN)
                .setOnPermissionRefusedListener(object : PickerBuilder.onPermissionRefusedListener {
                    override fun onPermissionRefused() {
                        Log.d(TAG, "onPermissionRefused: permission refused ")
                    }
                })
                .start()
    }

    /**
     * This method will start camera capture
     */
    private fun startCameraToCapture() {
        Log.d(TAG, "startCameraToCapture: from method")
        PickerBuilder(this@MyAccountScreen, PickerBuilder.SELECT_FROM_CAMERA)
                .setOnImageReceivedListener(object : PickerBuilder.onImageReceivedListener {
                    override fun onImageReceived(imageUri: Uri) {
                        // Toast.makeText(LookbookActivity.this, "Got image - " + imageUri, Toast.LENGTH_LONG).show();


                        Handler().postDelayed({
                            try {
                                //
                                profile_image.setImageBitmap(BitmapFactory.decodeFile(imageUri.encodedPath))

                                filepath(BitmapFactory.decodeFile(imageUri.encodedPath))

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }, 1000)

                    }
                })
                .setImageName("Marcuscinemas")
                .setImageFolderName("marcusfolder")
                .withTimeStamp(false)
                .setCropScreenColor(Color.CYAN)
                .setOnPermissionRefusedListener(object : PickerBuilder.onPermissionRefusedListener {
                    override fun onPermissionRefused() {
                        Log.d(TAG, "onPermissionRefused: refused camera")
                    }
                })
                .start()
    }


    private fun filepath(imageUri: Bitmap) {

        val byteArrayOutputStream = ByteArrayOutputStream()
        imageUri.compress(Bitmap.CompressFormat.PNG, 10, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        strImage = Base64.encodeToString(byteArray, Base64.DEFAULT)


    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.animator.slide_from_left, R.animator.slide_to_right)

    }

}


