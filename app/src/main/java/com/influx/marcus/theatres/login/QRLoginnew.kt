package com.influx.marcus.theatres.login

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.SparseArray
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.samples.vision.barcodereader.BarcodeCapture
import com.google.android.gms.samples.vision.barcodereader.BarcodeGraphic
import com.google.android.gms.samples.vision.barcodereader.ui.camera.CameraSource
import com.google.android.gms.vision.barcode.Barcode
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiInterface
import com.influx.marcus.theatres.api.ApiModels.Status
import com.influx.marcus.theatres.api.ApiModels.UserInfoReq
import com.influx.marcus.theatres.api.RestClient
import com.influx.marcus.theatres.common.AppStorage
import com.influx.marcus.theatres.commonview.UtilDialog
import com.influx.marcus.theatres.utils.UtilsDialog
import org.jetbrains.anko.alert
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.belvi.mobilevisionbarcodescanner.BarcodeRetriever

class QRLoginnew : AppCompatActivity(), BarcodeRetriever {

    private lateinit var barcodeCapture: BarcodeCapture
    var uniqueid: String = ""
    var userid: String = "ABC@123"
    var name = "Test"
    var mobile = "4545454545"
    var emailid = "Test@gmail.com"
    lateinit var context: Context
    lateinit var imgBack: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrlogin_new)

        initViews()
    }

    companion object {
        val TAG: String = QRLoginnew::class.java.simpleName
        fun newInstance() = QRLoginnew()
    }

//    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        val rootView = inflater!!.inflate(R.layout.activity_qrlogin, container, false)
//        initViews(rootView)
//        return rootView
//    }

    private fun initViews() {
        context = this@QRLoginnew
        AppStorage.putString(AppStorage.QRID, "", context)
        // get the barcode reader instance
        imgBack = findViewById(R.id.imgBack)

        barcodeCapture = supportFragmentManager.findFragmentById(R.id.barcode_scanner) as BarcodeCapture
        barcodeCapture.setRetrieval(this)
        barcodeCapture.isShowDrawRect = true
        barcodeCapture.setCameraFacing(CameraSource.CAMERA_FACING_BACK)
        imgBack.setOnClickListener({ onBack() })

    }

    override fun onBitmapScanned(sparseArray: SparseArray<Barcode>?) {

    }

    /*   override fun onScannedMultiple(barcodes: MutableList<Barcode>?) {
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
           Toast.makeText(context, "Error occurred while scanning " +
                   errorMessage, Toast.LENGTH_SHORT).show();

       }*/

    fun callLoginAPI(uniqueidd: String, userid: String, name: String, mobile: String, emailid: String) {
        UtilDialog.showProgressDialog(context, "")

        val userreq = UserInfoReq(uniqueidd, userid, name, mobile, emailid,
                AppStorage.getString(AppStorage.DEVICE_TOKEN, context))
        val apiInterface: ApiInterface = RestClient.getApiClient()
        val userinfo: Call<Status> = apiInterface.getUserinfo(userreq)
        userinfo.enqueue(object : Callback<Status> {
            override fun onFailure(call: Call<Status>?, t: Throwable?) {
                UtilDialog.hideProgress()
                alert("Please try again",
                        getString(R.string.marcus_theatre_title)) {
                    positiveButton("OK") {
                        it.dismiss()
                    }
                }.show()

            }

            override fun onResponse(call: Call<Status>?, response: Response<Status>?) {
                UtilDialog.hideProgress()
                try {
                    if (response?.body() != null) {
                        if (response.isSuccessful) {
                            AppStorage.putBoolean(AppStorage.isFromLogin,true,context)
                            alert("Login Successful", "Marcus Theatres") {

                                positiveButton("OK") { dialog ->
                                    dialog.dismiss()
                                }
                            }.show()
//                            val intent = Intent(context, BookingReview::class.java)
//                            startActivity(intent)
                            finish()
                        } else {
                            AppStorage.putBoolean(AppStorage.isFromLogin,false,context)
                            alert("Login Failure", "Marcus Theatres") {

                                positiveButton("OK") { dialog ->
                                    dialog.dismiss()
                                }
                            }.show()
                        }
                    } else {
                        AppStorage.putBoolean(AppStorage.isFromLogin,false,context)
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

    override fun onRetrieved(barcode: Barcode?) {
        if (barcode != null) {
            try {
                uniqueid = ""
                // playing barcode reader beep sound
                Log.d("QR::", barcode?.displayValue)
                if (!barcode?.displayValue!!.equals("http://commons.wikimedia.org/wiki/Main_Page")) {
                    uniqueid = barcode?.displayValue!!
                    AppStorage.putString(AppStorage.QRID, uniqueid, context)

                    if (UtilsDialog.isNetworkStatusAvialable(context)) {
                        callLoginAPI(uniqueid, userid, name, mobile, emailid)
                    }
                }
//                else {
//                    AppStorage.putString(AppStorage.QRID, "", context)
//                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onRetrievedMultiple(barcode: Barcode?, list: MutableList<BarcodeGraphic>?) {

        runOnUiThread {
            var message = "Code selected : " + barcode?.displayValue + "\n\nother " +
                    "codes in frame include : \n"
            for (index in list!!.indices) {
                val barcode = list.get(index).getBarcode()
                message += (index + 1).toString() + ". " + barcode.displayValue + "\n"
            }
            val builder = AlertDialog.Builder(this@QRLoginnew)
                    .setTitle("code retrieved")
                    .setMessage(message)
            builder.show()
        }
    }

    override fun onRetrievedFailed(p0: String?) {
    }

    override fun onPermissionRequestDenied() {
    }

    fun onBack() {
        finish()
        overridePendingTransition(R.animator.slide_from_left , R.animator.slide_to_right)
    }

    override fun onBackPressed() {
        onBack()
    }
}
