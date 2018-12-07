package com.influx.marcus.theatres.utils

import android.app.Dialog
import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Build
import android.support.constraint.ConstraintLayout
import android.view.*
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import com.influx.marcus.theatres.R
import java.util.*


/**
 * Created by Kavitha on 02-04-2018.
 */
class UtilsDialog {
    companion object {
        private var mDialog: Dialog? = null
        var popup: PopupWindow? = null
        fun showProgressDialog(mContext: Context, strMessage: String) {
            try {
                if (mDialog != null) {
                    if (mDialog!!.isShowing) {
                        mDialog!!.dismiss()
                    }
                }
                mDialog = Dialog(mContext)
                mDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
                val window = mDialog!!.getWindow()
                mDialog!!.setContentView(R.layout.progress_dialog)
                window!!.setGravity(Gravity.CENTER)
                mDialog!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
                mDialog!!.setCancelable(false)
                mDialog!!.show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun showProgressBlackDialog(mContext: Context, strMessage: String) {
            try {
                if (mDialog != null) {
                    if (mDialog!!.isShowing) {
                        mDialog!!.dismiss()
                    }
                }
                mDialog = Dialog(mContext, R.style.CustomDialogTheme)
                val window = mDialog!!.getWindow()
                mDialog!!.setContentView(R.layout.progress_dialog_80)
                window!!.setGravity(Gravity.CENTER)
                window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
                mDialog!!.setCancelable(false)
                mDialog!!.show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun hideProgress() {
            try {
                if (mDialog != null && mDialog!!.isShowing) {
                    mDialog!!.dismiss()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun isNetworkStatusAvialable(context: Context): Boolean {

            if (!isNetworkAvailable(context)) {
             //   Toast.makeText(context, "You seem to be offline. Please check your internet connection.", Toast.LENGTH_LONG).show()

                  val i = Intent (context,NoInternetActivity::class.java)
                  context.startActivity(i)

//                val dialog = Dialog(context)
//                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//                val window = dialog.window
//                dialog.setContentView(R.layout.no_internet_layout)
//                window!!.setGravity(Gravity.CENTER)
//                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
//                dialog.setCancelable(false)
//                val btnDismis = dialog.findViewById<View>(R.id.retry) as Button
//                dialog.setCanceledOnTouchOutside(false)
//                dialog.show()
//                btnDismis.setOnClickListener {
//                    val refresh = Intent(context, context.javaClass)
//                    context.startActivity(refresh)
//                    (context as Activity).finish()
//                    dialog.dismiss()
//                }
                return false
            } else {
                return true
            }


        }


//            val conMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//            val netInfo = conMgr.activeNetworkInfo
//            if (netInfo == null || !netInfo.isConnected || !netInfo.isAvailable) {
//                Toast.makeText(context, "Network Not Available! Please turn on Wifi or use mobile data.",
//                        Toast.LENGTH_SHORT);
//                val dialog = Dialog(context)
//                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//                val window = dialog.window
//                dialog.setContentView(R.layout.no_internet_layout)
//                window!!.setGravity(Gravity.CENTER)
//                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
//                dialog.setCancelable(false)
//                val btnDismis = dialog.findViewById<View>(R.id.retry) as Button
//                dialog.setCanceledOnTouchOutside(false)
//                dialog.show()
//                btnDismis.setOnClickListener {
//                    val refresh = Intent(context, context.javaClass)
//                    context.startActivity(refresh)
//                    (context as Activity).finish()
//                    dialog.dismiss()
////                }
//                return false
//            }
        //return true


        /**
         * helper method to detect internet
         */
        private fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }

        fun showSeatPopup(context: Context, seatNum: String) {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val window = dialog.window
            dialog.setContentView(R.layout.popup_seat)
            window!!.setGravity(Gravity.CENTER)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
            dialog.setCancelable(true)
            dialog.setCanceledOnTouchOutside(true)
            val txtWhoSitting = dialog.findViewById<View>(R.id.txtWhositting) as TextView
            val checkApply = dialog.findViewById<View>(R.id.checlApplyall)

            txtWhoSitting.text = context.getString(R.string.txt_who_sitting) + " " + seatNum

            dialog.setCanceledOnTouchOutside(false)
            dialog.show()

        }

        fun shopopupwindow(context: Context, x: Int, y: Int) {
            val inflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater

            // Inflate the custom layout/view
            val customView = inflater.inflate(R.layout.popup_seat, null)

            /*
                    public PopupWindow (View contentView, int width, int height)
                        Create a new non focusable popup window which can display the contentView.
                        The dimension of the window must be passed to this constructor.

                        The popup does not provide any background. This should be handled by
                        the content view.

                    Parameters
                        contentView : the popup's content
                        width : the popup's width
                        height : the popup's height
                */
            // Initialize a new instance of popup window
            var mPopupWindow = PopupWindow(
                    customView,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            if (popup != null && popup!!.isShowing) {
                popup?.dismiss()
            }
            popup = PopupWindow(context)
            popup?.contentView = customView
            popup?.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
            popup?.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
            popup?.isFocusable = true;
            // Some offset to align the popup a bit to the right, and a bit down, relative to button's position.
            val OFFSET_X = 30
            val OFFSET_Y = 30

            // Set an elevation value for popup window
            // Call requires API level 21
            if (Build.VERSION.SDK_INT >= 21) {
                mPopupWindow.elevation = 5.0f
            }
            mPopupWindow.setOnDismissListener { mPopupWindow.dismiss() }
            mPopupWindow.showAtLocation(customView, Gravity.NO_GRAVITY, x + OFFSET_X, y + OFFSET_Y)

//            // Get a reference for the custom view close button
//            val closeButton = customView.findViewById(R.id.ib_close) as ImageButton
//
//            // Set a click listener for the popup window close button
//            closeButton.setOnClickListener {
//                // Dismiss the popup window
//                mPopupWindow.dismiss()
//            }

        }

        fun getAge(year: Int, month: Int, day: Int): Boolean {
            try {
                val dob = Calendar.getInstance()
                val today = Calendar.getInstance()
                dob.set(year, month, day)
                val monthToday = today.get(Calendar.MONTH) + 1
                val monthDOB = dob.get(Calendar.MONTH) + 1
                val age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
                if (age > 13) {
                    LogUtils.d("AGE", " year more then 13")
                    return true
                } else if (age == 13) {

                    if (monthDOB > monthToday) {
                        LogUtils.d("AGE", " year  13 , month greater")
                        return true
                    } else if (monthDOB == monthToday) {
                        val todayDate = today.get(Calendar.DAY_OF_MONTH)
                        val dobDate = dob.get(Calendar.DAY_OF_MONTH)
                        return if (dobDate <= todayDate) {
                            LogUtils.d("AGE", " year  13 , month equal and day greater")
                            true
                        } else {
                            false
                        }
                    } else {
                        return false
                    }
                } else {
                    return false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false
        }
    }


}