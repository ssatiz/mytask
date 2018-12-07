package com.influx.marcus.theatres.theatres.NonGPSLocs

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.influx.marcus.theatres.api.ApiModels.OurTheatresNonGPS.Theatre
import com.influx.marcus.theatres.theatres.OurTheatres
import kotlinx.android.synthetic.main.activity_our_theatres.*

class CustomAutoCompleteTextChangedListener(internal var context: Context) : TextWatcher {

    override fun afterTextChanged(s: Editable) {
        // TODO Auto-generated method stub

    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int,
                                   after: Int) {
        // TODO Auto-generated method stub

    }

    override fun onTextChanged(userInput: CharSequence, start: Int, before: Int, count: Int) {

        val mainActivity = context as OurTheatres

        if(userInput.length > 0) {
            filterByText(userInput.toString())
            mainActivity.rvSearch.visibility = View.VISIBLE
            mainActivity.clOurTheatres.visibility = View.GONE
        }else{
            mainActivity.rvSearch.visibility = View.GONE
            mainActivity.clOurTheatres.visibility = View.VISIBLE
        }

    }

    private fun filterByText(userInput: String) {

        try {
            val mainActivity = context as OurTheatres
            //mainActivity.searchAdapter.notifyDataSetChanged()

            // val myObjs = mainActivity.nonallTheatres
            //val myObjs: ArrayList<com.ncrcinema.movietime.marcus.api.ApiModels.OurTheatresNonGPS.AllTheatre> = ArrayList<com.ncrcinema.movietime.marcus.api.ApiModels.OurTheatresNonGPS.AllTheatre>()
            val myTheatre: ArrayList<Theatre> = ArrayList<Theatre>()
            for (j in 0 until mainActivity.nonallTheatres.size){
                for (eachTheatre in mainActivity.nonallTheatres.get(j).theatres) {
                    if (eachTheatre.full_address.toLowerCase().contains(userInput.toLowerCase())) {
                        myTheatre.add(eachTheatre)
                    }
                }
            }
            mainActivity.searchAdapter = AutocompleteCustomArrayAdapter(mainActivity, myTheatre)
            mainActivity.rvSearch.setAdapter(mainActivity.searchAdapter)

        } catch (e: NullPointerException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}