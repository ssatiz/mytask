package com.influx.marcus.theatres.theatres.NonGPSLocs

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.OurTheatresNonGPS.Theatre
import com.influx.marcus.theatres.theatres.OurTheatres


class LocationListFragment : Fragment() {

    var theatrelists: List<Theatre>? = null
    var locationsAdapter: LocationsAdapter? = null
    private lateinit var rvCinemaLocs: RecyclerView
    var tvNoDataOutTheatre: TextView? = null
    var mTabname = ""


    fun getTabname(): String {
        return mTabname
    }

    fun setTabname(mTabname: String) {
        this.mTabname = mTabname
    }

    companion object {
        lateinit var fetchTheatresListener: OurTheatres.fetchTheatreShowTime
        lateinit var theatreLocations: List<Theatre>

        fun newInstance(tabname: String, theatresList: List<Theatre>, theatreReqListener: OurTheatres.fetchTheatreShowTime): LocationListFragment {
            theatreLocations = theatresList
            val tabFragment = LocationListFragment()
            tabFragment.setTabname(tabname)
            fetchTheatresListener = theatreReqListener
            return tabFragment
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_location_list,
                container, false)
        initViews(rootView)
        return rootView
    }

    private fun initViews(rootView: View?) {
        rvCinemaLocs = rootView!!.findViewById(R.id.rvCinemaLocations)
        tvNoDataOutTheatre = rootView!!.findViewById(R.id.tvNoDataOutTheatre) as TextView
        rvCinemaLocs.layoutManager =
                LinearLayoutManager(this!!.activity, LinearLayoutManager.VERTICAL, false)

        rvCinemaLocs.setNestedScrollingEnabled(false)

        try {
            val bundle = arguments
            theatrelists = ((bundle!!.getSerializable("theatrelist") as? List<Theatre>)!!)
            arguments!!.remove("theatrelist")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (theatrelists != null) {
            rvCinemaLocs.visibility = View.VISIBLE
            tvNoDataOutTheatre!!.visibility = View.GONE

            locationsAdapter = LocationsAdapter(this.context!!, theatrelists!!, fetchTheatresListener)
            val mLayoutManager = LinearLayoutManager(context!!.applicationContext)
            rvCinemaLocs.setLayoutManager(mLayoutManager)
            rvCinemaLocs.setItemAnimator(DefaultItemAnimator())
            rvCinemaLocs.getRecycledViewPool().clear()
            rvCinemaLocs.setAdapter(locationsAdapter)
        } else {
            rvCinemaLocs.visibility = View.GONE
            tvNoDataOutTheatre!!.visibility = View.VISIBLE
        }

        /*rvCinemaLocs.addOnItemTouchListener(RecyclerTouchListener(activity,
                RecyclerTouchListener.OnItemClickListener { view, position ->
                    val i = Intent(context, TheatreShowTimeActivity::class.java)
                    AppConstants.putString(AppConstants.KEY_THEATRECODE, theatrelists!!.get(position).code, context!!)
                    AppConstants.putString(AppConstants.KEY_THEATREIMAGE, theatrelists!!.get(position).image_url, context!!)
                    AppConstants.putString(AppConstants.KEY_THEATRENAME, theatrelists!!.get(position).name, context!!)
                    context!!.startActivity(i)
                }
        ))*/

    }
}
