package com.influx.marcus.theatres.moviefilter

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.filter.Cinema


class LocationFragment : Fragment(), clearLocationData {

    lateinit var rvCinema: RecyclerView
    lateinit var etSearchByMve: EditText
    lateinit var mcontext: Context
    private lateinit var uiHelper: filterUiHelper
    private lateinit var mAdapter: LocationAdapter
    private var nonFilteredData: ArrayList<Cinema> = ArrayList()

    companion object {
        lateinit var AllCinemasListOriginal: List<Cinema>
        fun newInstance(resp: List<Cinema>, uiListener: filterUiHelper) = LocationFragment().apply {
            AllCinemasListOriginal = resp
            uiHelper = uiListener
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater!!.inflate(R.layout.fragment_filterlocation, container,
                false)
        mcontext = (this.activity as Context?)!!
        nonFilteredData.addAll(AllCinemasListOriginal)
        rvCinema = rootView.findViewById(R.id.rvCinema)
        rvCinema.setLayoutManager(LinearLayoutManager(mcontext, LinearLayoutManager.VERTICAL, false))
        mAdapter = LocationAdapter(AllCinemasListOriginal, mcontext, selectedCinemaListener)
        rvCinema.adapter = mAdapter

        /**
         *  rvCinema.addOnItemTouchListener(RecyclerTouchListener(activity, RecyclerTouchListener.OnItemClickListener { view, position ->
        val item = cinemas.get(position)

        }
         */
        etSearchByMve = rootView.findViewById(R.id.etSearchByMve)
        etSearchByMve.setImeOptions(EditorInfo.IME_ACTION_DONE)
        etSearchByMve.setSingleLine()
        etSearchByMve.setPressed(true)
        etSearchByMve.setOnTouchListener(View.OnTouchListener { v, event ->
            etSearchByMve.isCursorVisible = true
            false
        })
        etSearchByMve.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                if (cs.length == 0) {
                    mAdapter = LocationAdapter(AllCinemasListOriginal, mcontext, selectedCinemaListener)
                    rvCinema.adapter = mAdapter
                } else {
                    filterByText(cs.toString())

                }
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int,
                                           arg3: Int) {

            }

            override fun afterTextChanged(arg0: Editable) {

            }
        })
        return rootView
    }


    private fun filterByText(search: String) {
        var filteredCinemas = ArrayList<Cinema>()

        for (eachMovie in AllCinemasListOriginal) {
            if (eachMovie.full_address.contains(search, ignoreCase = true)) {
                filteredCinemas.add(eachMovie)
            }
        }

        val mAdapter = LocationAdapter(filteredCinemas, mcontext, selectedCinemaListener)
        rvCinema.adapter = mAdapter
    }

    private var selectedCinemaListener = object : LocationAdapter.SelectedCinemaListener {
        override fun select( preferedCinema: String) {
            uiHelper.enableSavebutton()
            uiHelper.enableResetButton()
        }

        override fun unselect(size: Int) {
            if (size == 0) {
                uiHelper.disableSaveButton()
                uiHelper.disableResetButton()

            }

        }
    }

    override fun clearData() {
        for (eachItem in nonFilteredData) {
            eachItem.isSelect = false
        }
        val mAdapter = LocationAdapter(nonFilteredData, mcontext, selectedCinemaListener)
        rvCinema.adapter = mAdapter
    }
}

interface clearLocationData {
    fun clearData()
}
