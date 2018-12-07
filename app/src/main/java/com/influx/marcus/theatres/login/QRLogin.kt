package com.influx.marcus.theatres.login

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.SparseArray
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.vision.barcode.Barcode
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiInterface
import com.influx.marcus.theatres.api.ApiModels.Status
import com.influx.marcus.theatres.api.ApiModels.UserInfoReq
import com.influx.marcus.theatres.api.RestClient
import com.influx.marcus.theatres.common.AppStorage
import com.influx.marcus.theatres.commonview.UtilDialog
import com.influx.marcus.theatres.utils.UtilsDialog
import info.androidhive.barcode.BarcodeReader
import org.jetbrains.anko.alert
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QRLogin : AppCompatActivity(), BarcodeReader.BarcodeReaderListener {
    lateinit var barcodereader: BarcodeReader
    var uniqueid: String = ""
    var userid: String = "ABC@123"
    var name = "Test"
    var mobile = "4545454545"
    var emailid = "Test@gmail.com"
    lateinit var context: Context
    lateinit var imgBack: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrlogin)

        initViews()
    }

    companion object {
        val TAG: String = QRLogin::class.java.simpleName
        fun newInstance() = QRLogin()
    }

//    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        val rootView = inflater!!.inflate(R.layout.activity_qrlogin, container, false)
//        initViews(rootView)
//        return rootView
//    }

    private fun initViews() {
        context = this@QRLogin
        AppStorage.putString(AppStorage.QRID, "", context)
        // get the barcode reader instance
        barcodereader = supportFragmentManager.findFragmentById(R.id.barcode_scanner) as BarcodeReader
        imgBack = findViewById(R.id.imgBack)
        imgBack.setOnClickListener({ onBack() })

    }

    override fun onBitmapScanned(sparseArray: SparseArray<Barcode>?) {

    }

    override fun onScannedMultiple(barcodes: MutableList<Barcode>?) {
    }

    override fun onScanned(barcode: Barcode?) {
        try {
            uniqueid = ""
// playing barcode reader beep sound
            barcodereader.playBeep()
            Log.d("QR::", barcode?.displayValue)
            if (!barcode?.displayValue!!.equals("http://commons.wikimedia.org/wiki/Main_Page")) {
                uniqueid = barcode?.displayValue!!
                AppStorage.putString(AppStorage.QRID, uniqueid, context)

                if (UtilsDialog.isNetworkStatusAvialable(context)) {
                    callLoginAPI(uniqueid, userid, name, mobile, emailid)
                }
            }else{
                AppStorage.putString(AppStorage.QRID, "", context)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onScanError(errorMessage: String?) {
        alert("Error occurred while scanning " +
                errorMessage,
                getString(R.string.marcus_theatre_title)) {
            positiveButton("OK") {
                it.dismiss()
            }
        }.show()


    }

    fun callLoginAPI(uniqueidd: String, userid: String, name: String, mobile: String, emailid: String) {
        UtilDialog.showProgressDialog(context, "")

        val userreq = UserInfoReq(uniqueidd, userid, name, mobile, emailid,
                AppStorage.getString(AppStorage.DEVICE_TOKEN, context))
        val apiInterface: ApiInterface = RestClient.getApiClient()
        val userinfo: Call<Status> = apiInterface.getUserinfo(userreq)
        userinfo.enqueue(object : Callback<Status> {
            override fun onFailure(call: Call<Status>?, t: Throwable?) {
                UtilDialog.hideProgress()
                alert(getString(R.string.ohinternalservererror),
                        getString(R.string.marcus_theatre_title)) {
                    positiveButton("OK") {
                        it.dismiss()
                    }
                }.show()            }

            override fun onResponse(call: Call<Status>?, response: Response<Status>?) {
                UtilDialog.hideProgress()
                try {
                    if (response?.body() != null) {
                        if (response.isSuccessful) {
                            alert("Login Successful",
                                    getString(R.string.marcus_theatre_title)) {
                                positiveButton("OK") {
                                    it.dismiss()
                                }
                            }.show()
//                            val intent = Intent(context, BookingReview::class.java)
//                            startActivity(intent)
                            finish()
                        } else {
                            alert("Login Failure",
                                    getString(R.string.marcus_theatre_title)) {
                                positiveButton("OK") {
                                    it.dismiss()
                                }
                            }.show()
                        }
                    } else {
                        alert("Please try again", "Marcus Theatres") {

                            positiveButton("OK") { dialog ->
                                dialog.dismiss()
                            }
                        }.show()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        })
    }

    fun onBack() {
        finish()
    }

    override fun onBackPressed() {
        onBack()
    }
}
