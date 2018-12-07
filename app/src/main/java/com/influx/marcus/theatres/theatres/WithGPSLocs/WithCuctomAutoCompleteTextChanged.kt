package com.influx.marcus.theatres.theatres.WithGPSLocs

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.influx.marcus.theatres.api.ApiModels.OurTheatresGPS.AllTheatre
import com.influx.marcus.theatres.theatres.OurTheatres
import kotlinx.android.synthetic.main.activity_our_theatres.*

class WithCuctomAutoCompleteTextChanged(internal var context: Context) : TextWatcher {

    override fun afterTextChanged(s: Editable) {
        // TODO Auto-generated method stub

    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int,
                                   after: Int) {
        // TODO Auto-generated method stub

    }

    override fun onTextChanged(userInput: CharSequence, start: Int, before: Int, count: Int) {

        val mainActivity = context as OurTheatres

        if (userInput.length > 0) {
            filterByText(userInput.toString())
            mainActivity.rvSearch.visibility = View.VISIBLE
            mainActivity.clOurTheatres.visibility = View.GONE
        } else {
            mainActivity.rvSearch.visibility = View.GONE
            mainActivity.clOurTheatres.visibility = View.VISIBLE
        }

    }

    private fun filterByText(userInput: String) {

        try {
            val mainActivity = context as OurTheatres
            //mainActivity.searchAdapter.notifyDataSetChanged()

            // val myObjs = mainActivity.nonallTheatres
            val myObjs: ArrayList<AllTheatre> = ArrayList<AllTheatre>()

            for (eachTheatre in mainActivity.allTheatres) {
                if (eachTheatre.full_address.toLowerCase().contains(userInput.toLowerCase())) {
                    myObjs.add(eachTheatre)
                }
            }
            mainActivity.withsearchAdapter = WithAutocompleteCustomAdapter(mainActivity, myObjs)
            mainActivity.rvSearch.setAdapter(mainActivity.withsearchAdapter)

        } catch (e: NullPointerException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}