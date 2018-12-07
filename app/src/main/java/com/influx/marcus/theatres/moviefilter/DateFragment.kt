package com.influx.marcus.theatres.moviefilter

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.utils.AppConstants
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class DateFragment : Fragment(), clearDate, clearLocationData {


    private lateinit var uiHelper: filterUiHelper
    lateinit var mcontext: Context
    lateinit var date: EditText
    lateinit var dpd: DatePickerDialog

    companion object {
        var dateCount: Int = 0

        fun newInstance(resp: Int, uiListener: filterUiHelper) = DateFragment().apply {
            dateCount = resp
            uiHelper = uiListener
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mcontext = (this.activity as Context?)!!
        var rootView: View = inflater.inflate(R.layout.fragment_date, container, false)
        date = rootView.findViewById(R.id.editDOB)
        if (AppConstants.getString(AppConstants.KEY_FILTERDATE, mcontext) != null && AppConstants.getString(AppConstants.KEY_FILTERDATE, mcontext).isNotEmpty()) {
            date.setText(AppConstants.getString(AppConstants.KEY_FILTERDATE, mcontext))
            uiHelper.enableResetButton()
            uiHelper.enableSavebutton()
        }

        selectDate()
        date.setOnTouchListener(View.OnTouchListener { v, event ->
            dpd.show()
            false
        })

        return rootView
    }

    fun selectDate() {
        val c = Calendar.getInstance()
        val mYear = c.get(Calendar.YEAR);
        val mMonth = c.get(Calendar.MONTH);
        val mDay = c.get(Calendar.DAY_OF_MONTH);
        dpd = DatePickerDialog(mcontext, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            val sdf = SimpleDateFormat("MM/dd/yyyy")
            val strdt = (monthOfYear + 1).toString() + "/" + dayOfMonth.toString() + "/" + year
            var strDate: Date? = null
            try {
                strDate = sdf.parse(strdt)
                date.setText((monthOfYear + 1).toString() + "/" + dayOfMonth.toString() + "/" + year)
                uiHelper.enableSavebutton()
                uiHelper.enableResetButton()
                val stDate = (monthOfYear + 1).toString() + "/" + dayOfMonth.toString() + "/" + year
                AppConstants.putString(AppConstants.KEY_FILTERDATE, strdt, mcontext)
                AppConstants.putString(AppConstants.KEY_FILTERLOCDATE, stDate, mcontext)

            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }, mYear, mMonth, mDay)
        c.add(Calendar.DATE, dateCount)
        dpd.getDatePicker().setMaxDate(c.getTimeInMillis())
        dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
    }

    override fun clearData() {
        date.setText("")
    }
}

interface clearDate {
    fun clearData()
}