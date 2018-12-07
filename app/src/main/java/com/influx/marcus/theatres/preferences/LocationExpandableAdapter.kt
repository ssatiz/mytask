package com.influx.marcus.theatres.preferences

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.preferedlocations.subval.Data
import com.influx.marcus.theatres.api.pref.CinemaDetail


import com.influx.marcus.theatres.utils.AppConstants
import org.jetbrains.anko.alert
import org.jetbrains.anko.support.v4.alert
import java.lang.Exception


class ExpandableListAdapter(private val _context: Context,
                            private val cityList: MutableList<String>,
                            private val cinemasList: HashMap<String, MutableList<Data>>,
                            expandableListAdapterListener: ActivityUiHelper, expandableListlistener: ExpandableListListener) :
        BaseExpandableListAdapter() {

    private var localCityList: MutableList<String> = cityList
    private var localcinemaList: HashMap<String, MutableList<Data>> = cinemasList
    private var count: Int = 0
    var onClickListener: ActivityUiHelper = expandableListAdapterListener
    val context = _context
    private val parentActivity = context as PreferenceActivity
    var expandableListlistener = expandableListlistener
    var select = false
    var selectvalue = 0

    interface ExpandableListAdapterListener {
        fun expand(v: View, select: Boolean)
    }

    interface ExpandableListListener {
        fun select(v: View?, name: String, prefer: Boolean,code: String)
        fun unselect()
        fun onlyAddToHorizontalRv(cinemaname:String)
    }

    override fun getChild(groupPosition: Int, childPosititon: Int): Any {
        return this.localcinemaList[this.localCityList[groupPosition]]!!.get(childPosititon)
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getChildView(groupPosition: Int, childPosition: Int,
                              isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        //val childText = getChild(groupPosition, childPosition) as String
        val cinemaData: Data = getChild(groupPosition, childPosition) as Data

        if (convertView == null) {
            val infalInflater = this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = infalInflater.inflate(R.layout.locationchilditem, null)
        }

        val txtListChild = convertView!!
                .findViewById(R.id.locationChildItem) as TextView
        val tvMiles = convertView!!.findViewById<TextView>(R.id.tvMiles)
        val select: ImageView = convertView!!.findViewById(R.id.select) as ImageView
        val unselect: ImageView = convertView!!.findViewById(R.id.unselect) as ImageView
        if (cinemaData.isPrefered) {
            select.setVisibility(View.VISIBLE)
            unselect.setVisibility(View.GONE)
            onClickListener.enableSaveButton()
        } else {
            unselect.setVisibility(View.VISIBLE)
            select.setVisibility(View.GONE)
        }
        txtListChild.text = cinemaData.name
        if (cinemaData.miles_str != null) {
            tvMiles.text = " - ${cinemaData.miles_str}"
        }
        convertView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

                if (cinemaData.isPrefered) {
                    // reduce
                    unselect.setVisibility(View.VISIBLE)
                    select.setVisibility(View.GONE)
                    cinemaData.isPrefered = false
                    expandableListlistener.select(v!!, cinemaData.name, cinemaData.isPrefered,cinemaData.code)
                    if (cinemaData.code in AppConstants.cinemaList) AppConstants.cinemaList.remove(cinemaData.code)

                    lateinit var loctobeRemoved: CinemaDetail
                    for (eachLocalCinema in AppConstants.localCinemadata){
                        if (eachLocalCinema.cinemaCode.equals(cinemaData.code)) {
                            loctobeRemoved = eachLocalCinema
                        }
                    }
                    try {
                        AppConstants.localCinemadata.remove(loctobeRemoved)
                    }catch (e: Exception){
                        e.printStackTrace()
                    }

                    if (AppConstants.localCinemadata.size == 0) {
                        onClickListener.disableSaveButton()
                    }else{
                        onClickListener.enableSaveButton()
                    }
                    if (AppConstants.cinemaList.size == 0){
                        onClickListener.disableSaveButton()
                    } else {
                        onClickListener.enableSaveButton()
                    }
                } else {
                    // increase
                    if ( AppConstants.localCinemadata.size < 3) {
                        select.setVisibility(View.VISIBLE)
                        unselect.setVisibility(View.GONE)
                        cinemaData.isPrefered = true
                        if (cinemaData.code in AppConstants.cinemaList) {
                            //donothing already present
                        } else {
                            AppConstants.cinemaList.add(cinemaData.code)
                        }

                        var isCinemaPresent = false

                        for (eachLocalCinema in AppConstants.localCinemadata){
                            if (eachLocalCinema.cinemaCode.equals(cinemaData.code))isCinemaPresent=true
                        }
                        if (!isCinemaPresent) {
                            AppConstants.localCinemadata.add(CinemaDetail(cinemaData.name,
                                    cinemaData.code))
                        }

                        expandableListlistener.select(v!!, cinemaData.name, cinemaData.isPrefered,
                                cinemaData.code)
                        onClickListener.enableSaveButton()
                    } else {

                        context.alert("You may choose upto 3 cinemas",
                                context.getString(R.string.marcus_theatre_title)) {
                            positiveButton("OK") {
                                it.dismiss()
                            }
                        }.show()
                        onClickListener.enableSaveButton()
                    }
                }
            }
        })
        return convertView
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return this.localcinemaList[this.localCityList[groupPosition]]?.size!!
    }

    override fun getGroup(groupPosition: Int): Any {
        return this.localCityList[groupPosition]
    }

    override fun getGroupCount(): Int {
        return this.localCityList.size
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean,
                              convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val headerTitle = getGroup(groupPosition) as String
        if (convertView == null) {
            val infalInflater = this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = infalInflater.inflate(R.layout.listlocationhead, null)
        }
        val lblListHeader = convertView!!
                .findViewById(R.id.tvListHeader) as TextView
        val downImg = convertView!!.findViewById<ImageView>(R.id.ivDropDown)
        val lineView = convertView!!.findViewById<View>(R.id.view)
        // lblListHeader.setTypeface(null, Typeface.BOLD)
        lblListHeader.text = headerTitle


        if (isExpanded) {
            downImg.setImageResource(R.drawable.uparrow)
        } else {
            downImg.setImageResource(R.drawable.downarrow)
        }
        return convertView
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }
}