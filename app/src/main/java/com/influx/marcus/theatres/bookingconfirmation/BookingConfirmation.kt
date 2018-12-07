package com.influx.marcus.theatres.bookingconfirmation

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.github.florent37.runtimepermission.kotlin.PermissionException
import com.github.florent37.runtimepermission.kotlin.coroutines.experimental.askPermission
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.blockseat.BlockSeatResp
import com.influx.marcus.theatres.common.AppStorage
import com.influx.marcus.theatres.homepage.HomeActivity
import com.influx.marcus.theatres.utils.AppConstants
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.bookingconfirmation.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.alert
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.toast
import java.io.File
import java.io.FileOutputStream


class BookingConfirmation : AppCompatActivity() {
    lateinit var txtHome: TextView
    lateinit var context: Context
    lateinit var confirmationVM: ConfirmationVM
    lateinit var imagePath: File
    private lateinit var tvLocation: TextView
    var date: String = ""
    var movieimg: String = ""
    var expimg: String = ""
    var qrcode: String = ""
    var movietitle: String = ""
    var cinemaname: String = ""
    var orderid: String = ""
    var qrtext: String = ""
    var month: String = ""
    var cardType: String = ""
    var fname: String = ""
    var bookingid: String = ""
    var ticketCount: String = ""
    var screen: String = ""
    var success: String = ""
    var showtime: String = ""
    var exprience_single_logo: String = ""
    var bookingFailure: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bookingconfirmation)
        context = this@BookingConfirmation
        initViews()
    }

    fun initViews() {
        rlShare.visibility = View.VISIBLE
        ivExpand.visibility = View.VISIBLE
        ivHome.setOnClickListener({ navigateToHome() })
        // Picasso.with(context).load(R.drawable.sample_qr).into(imgQR)
        fname = AppConstants.getString(AppConstants.KEY_FIRSTNAME,context)

        ivShare.setOnClickListener({

            launch(UI) {
                try {
                    val result = askPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                    var shareTxt :String = ""
                    if (ticketCount.toInt() > 1) {
                         shareTxt = fname + " has shared a ticket with you for " +
                                movietitle + " at " + cinemaname + ". \nPlease show the confirmation code or barcode at the theatre for admission."
                    } else {
                         shareTxt = fname + " has shared a ticket with you for " +
                                movietitle + " at " + cinemaname + ". \nPlease show the confirmation code or barcode at the theatre for admission."
                    }
                    rlShare.visibility = View.GONE
                    ivExpand.visibility = View.GONE
                    shareIt(this@BookingConfirmation, shareTxt)
                } catch (e: PermissionException) {
                    rlShare.visibility = View.VISIBLE
                    ivExpand.visibility = View.VISIBLE
                    var isDenied = false
                    e.denied.forEach {
                        //toast("Accepted - ${it.toString()}")
                        isDenied = true
                    }
                    e.accepted.forEach {
                        //    toast("Accepted - ${it.toString()}")
                    }
                    if (isDenied) {
                        alert("Permission Denied, Please allow to share tickets",
                                getString(R.string.marcus_theatre_title)) {
                            positiveButton("OK") {
                                it.dismiss()
                            }
                        }.show()
                    }

                }
            }
        })


        tvLocation = findViewById(R.id.tvmovieLoc)
        val location = AppConstants.getString(AppConstants.KEY_CINEMALOCATION, this)
        tvLocation.setText(location)
        try {
            val blockresp: BlockSeatResp = AppConstants.getObject(AppConstants.KEY_BLOCKRESP,
                    this@BookingConfirmation, BlockSeatResp::class.java as Class<Any>) as BlockSeatResp
            val blockList = blockresp.DATA
            Log.i("Order Id", blockresp.DATA.order_id)
            orderid = blockresp.DATA.order_id
            if (intent.extras.getString("date") != null) {
                date = intent.extras.getString("date")
            }
            if (intent.extras.getBoolean("success") != null) {
                bookingFailure = intent.extras.getBoolean("success")
            }
            if (intent.extras.getString("movieimg") != null)
                movieimg = intent.extras.getString("movieimg")
            if (intent.extras.getString("expimg") != null)
                expimg = intent.extras.getString("expimg")
            if (intent.extras.getString("movietitle") != null)
                movietitle = intent.extras.getString("movietitle")
            if (intent.extras.getString("cinemaname") != null)
                cinemaname = intent.extras.getString("cinemaname")
            if (intent.extras.getString("qrcode") != null)
                qrcode = intent.extras.getString("qrcode")
            if (intent.extras.getString("qrtext") != null)
                qrtext = intent.extras.getString("qrtext")

            if (intent.extras.getString("ticketCount") != null)
                ticketCount = intent.extras.getString("ticketCount")
            if (intent.extras.getString("bookingid") != null)
                bookingid = intent.extras.getString("bookingid")
            if (intent.extras.getString("screen") != null)
                screen = intent.extras.getString("screen")
            if (intent.extras.getString("showtime") != null)
                showtime = intent.extras.getString("showtime")
            if (intent.hasExtra("exprience_single_logo")) {
                exprience_single_logo = intent.extras.getString("exprience_single_logo")
                if (exprience_single_logo.equals("0")) {
                    ivCinema.getLayoutParams().width = resources.getDimension(R.dimen.imageview_width).toInt()
                }
            }
            if (bookingFailure == false) {
                qrLayout.visibility = View.GONE
                ivShare.visibility = View.GONE
                tvSucces.setText(R.string.booking_failure)
                tvSucces.setTextColor(resources.getColor(R.color.marcus_red))
            } else {

                tvBookingId.text = "CONFIRMATION NUMBER : " + bookingid


            }
            tvDate.text = date
            val time12hrs = AppConstants.getString(AppConstants.KEY_TIME, context)
            tvTime.text = showtime
            if (movieimg.isNotBlank()) {
                Picasso.with(context).load(movieimg).into(ivImage, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {
                        // loader.visibility = View.GONE
                        ivImage.visibility = View.VISIBLE
                    }

                    override fun onError() {
                        // loader.visibility = View.GONE
                    }
                })
            }

            if (expimg.isNotBlank()) {
                Picasso.with(context).load(expimg).into(ivCinema)
            } else {
                ivCinema.visibility = View.GONE
                val expName = AppConstants.getString(AppConstants.KEY_EXPERIENCETEXT, context)
                if (expName.isNotBlank()) {
                    tvExperienceName.visibility = View.VISIBLE
                    tvExperienceName.setText(expName)
                }
            }

            if (movietitle.length > 18) {
                tvMovieName.textSize = 14f
            }
            tvMovieName.text = movietitle
            tvLocation.text = cinemaname + " - " + screen
            tvSeats.text = (AppConstants.getString(AppConstants.KEY_SEATSTR, context))
            if (qrcode.isNotBlank()) {
                rlv_loading.visibility = View.VISIBLE
                Picasso.with(context)
                        .load(qrcode)
                        .into(imgQR, object : com.squareup.picasso.Callback {
                            override fun onError() {
                                rlv_loading.visibility = View.GONE
                            }

                            override fun onSuccess() {
                                rlv_loading.visibility = View.GONE
                            }
                        })
            }
            txtQr.text = qrtext

            ivExpand.setOnClickListener {
                openQRPopup(qrcode)
            }
            imgQR.setOnClickListener({
                openQRPopup(qrcode)
            })

        }
        catch (e: Exception) {

        }

    }

    fun shareIt(context: Context, shareTxt: String) {
        val rootView = (context as Activity).window.decorView.findViewById<View>(android.R.id.content)

        val bm = screenShot(rootView)
        val file = saveBitmap(bm, "marcus_image.png")
        val uri = Uri.fromFile(File(file.absolutePath))
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareTxt)
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        shareIntent.type = "image/*"
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(Intent.createChooser(shareIntent, "share via"))
    }

    private fun screenShot(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun saveBitmap(bm: Bitmap, fileName: String): File {
        val path = Environment.getExternalStorageDirectory().absolutePath + "/Screenshots"
        val dir = File(path)
        if (!dir.exists())
            dir.mkdirs()
        val file = File(dir, fileName)
        try {
            val fOut = FileOutputStream(file)
            bm.compress(Bitmap.CompressFormat.PNG, 90, fOut)
            fOut.flush()
            fOut.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        rlShare.visibility = View.VISIBLE
        ivExpand.visibility = View.VISIBLE
        return file
    }


    private fun openQRPopup(imgUrl: String) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.popup_qrcodelayout);
        dialog.getWindow().setLayout(matchParent, matchParent)
        val ivQR: ImageView = dialog!!.findViewById(R.id.ivQrimg)
        val ivClose: ImageView = dialog!!.findViewById(R.id.ivClose)
        if (imgUrl.isNotBlank()) {
            Picasso.with(context)
                    .load(imgUrl)
                    .into(ivQR, object : com.squareup.picasso.Callback {
                        override fun onError() {
                            loader.visibility = View.GONE
                        }

                        override fun onSuccess() {
                            loader.visibility = View.GONE
                            ivQR.visibility = View.VISIBLE
                        }
                    })
        }
        ivClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show();
    }


    fun navigateToHome() {
        AppStorage.putString(AppStorage.QRID, "", context)
        AppStorage.putBoolean(AppStorage.isFromLogin, false, context)
        AppConstants.isFromNavDraw = false
        startActivity(Intent(context, HomeActivity::class.java))
        finish()
        overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)

    }

    override fun onBackPressed() {
        navigateToHome()
    }
}