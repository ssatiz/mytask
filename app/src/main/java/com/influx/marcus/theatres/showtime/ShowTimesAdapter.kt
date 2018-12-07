package com.influx.marcus.theatres.showtime

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.showtime.ShowtimeResponse
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.LogUtils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.expinfo_dialog.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk25.coroutines.onClick


class ShowTimesAdapter(val context: Context, val movieDataPerDate: ArrayList<ShowtimeResponse.DATa.MovieInfo>,
                       imgurl: String, movieName: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_DATA = 0
    private val VIEW_FOOTER = 1
    private var listRange = 2
    private var localList: ArrayList<ShowtimeResponse.DATa.MovieInfo> = ArrayList()
    lateinit var cname: String
    lateinit var expimage: String
    val imgurl = imgurl
    private val movieNameStr = movieName


    init {
        if (movieDataPerDate.size > 3) {
            for (item in 0..listRange) {
                localList.add(movieDataPerDate.get(item))
            }
            val loadMoreItem: ShowtimeResponse.DATa.MovieInfo = ShowtimeResponse.DATa.MovieInfo(null, null, true)
            localList.add(loadMoreItem)

        } else {
            localList = movieDataPerDate
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var rowView: View? = null
        when (viewType) {
            0 -> {
                rowView = LayoutInflater.from(context).inflate(R.layout.row_cinemasshowtime, parent, false)
                return ViewHolderData(rowView)
            }
            1 -> {
                rowView = LayoutInflater.from(context).inflate(R.layout.row_showmorefooter, parent, false)
                return ViewHolderFooter(rowView)
            }
            else -> {
                rowView = LayoutInflater.from(context).inflate(R.layout.row_showmorefooter, parent, false)
                return ViewHolderFooter(rowView)
            }
        }
    }

    override fun getItemCount(): Int {
        return localList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val eachItem = localList.get(position)
        when (holder.itemViewType) {
            0 -> {
                var maintananceFlag = false
                val dataHolder: ViewHolderData = holder as ViewHolderData
                dataHolder.tvCinemaName.text = eachItem.cinemas!!.cname.toUpperCase()
                if (eachItem.cinemas.miles != null && !eachItem.cinemas.miles.equals("", ignoreCase = true)) {
                    dataHolder.tvMiles.text = eachItem.cinemas.miles
                } else {
                    dataHolder.ivMiles.visibility = View.GONE
                    dataHolder.tvMiles.visibility = View.GONE
                }
                if (!eachItem.isPreferred) {
                    Picasso.with(context).load(R.drawable.whiteborderheart).into(holder.ivPreferedHeart)
                } else {
                    Picasso.with(context).load(R.drawable.redfilledheart).into(holder.ivPreferedHeart)
                }
                holder.tblShows.removeAllViews()
                val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val lastExpIndex = eachItem.cinemas.exp_data.size - 1

                val cinemaName = eachItem.cinemas.cname
                eachItem.cinemas.exp_data.forEachIndexed { index, expData ->
                    val expRow = inflater.inflate(R.layout.row_experienceheader, null)
                    val ivExp = expRow.findViewById<ImageView>(R.id.ivExperienceLogo)
                    val ivInfo = expRow.findViewById<ImageView>(R.id.ivExperienceInfo)
                    val tvMoreExp = expRow.findViewById<TextView>(R.id.tvMoreExpDesc)
                    val tvExperienceName = expRow.findViewById<TextView>(R.id.tvExpName)
                    val ivMoreLogo = expRow.findViewById<ImageView>(R.id.ivIndineLogo)
                    val clDineLayout = expRow.findViewById<ConstraintLayout>(R.id.clDineIn)
                    val clMaintanance = expRow.findViewById<ConstraintLayout>(R.id.clMaintanance)
                    val tvMaintananceDesc = expRow.findViewById<TextView>(R.id.tvMaintananceDesc)
                    val ivMaintananceLogo = expRow.findViewById<ImageView>(R.id.ivMaintananceLogo)
                    val cid = eachItem.cinemas.cid
                    val ccode = eachItem.cinemas.ccode
                    expimage = expData.exp_image

                    if (expData.maintenance != null) {
                        if (expData.maintenance.desc.isNullOrBlank()) {
                            clMaintanance.visibility = View.GONE
                        } else {
                            clMaintanance.visibility = View.VISIBLE
                            tvMaintananceDesc.setText(expData.maintenance.desc)
                            maintananceFlag = true
                            if (expData.maintenance.icon!!.isNotBlank()) {
                                Picasso.with(context)
                                        .load(expData.maintenance.icon).into(ivMaintananceLogo)
                            } else {
                                ivMaintananceLogo.visibility = View.GONE
                            }
                        }
                    }
                    if (expData.more_desc != null) {
                        if (expData.more_desc.desc.isNullOrBlank()) {
                            clDineLayout.visibility = View.GONE
                        } else {
                            LogUtils.d("SHOWTIMEADAPTER", "data is ${expData.more_desc.desc}")
                            clDineLayout.visibility = View.VISIBLE
                            tvMoreExp.setText(expData.more_desc.desc)
                            if (expData.more_desc.icon!!.isNotBlank()) {
                                Picasso.with(context)
                                        .load(expData.more_desc.icon).into(ivMoreLogo)
                            } else {
                                ivMoreLogo.visibility = View.GONE
                            }
                        }
                    } else {
                        clDineLayout.visibility = View.GONE
                    }


                    if (expData.exp_image.isNotBlank()) {
                        Picasso.with(context).load(expData.exp_image).into(ivExp)

                    } else {
                        ivExp.visibility = View.GONE
                        if (expData.exp_name.length > 30) {
                            tvExperienceName.textSize = 10f
                        }
                        tvExperienceName.setText(expData.exp_name.toUpperCase())
                    }
                    if (expData.exp_desc.isNotBlank()) {
                        ivInfo.setOnClickListener {
                            showPopupExperienceInfo(expData)
                        }
                    } else {
                        ivInfo.visibility = View.GONE
                    }
                    expRow.id = index
                    holder.tblShows.addView(expRow)

                    val parent = GridLayout(context)
                    parent.columnCount = 4

                    val params = GridLayout.LayoutParams(GridLayout.spec(
                            GridLayout.UNDEFINED, GridLayout.FILL, 1f),
                            GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f))
                    parent.layoutParams = params
                    val lastTimeIndex = expData.session_data.size - 1
                    if (!maintananceFlag) {
                        expData.session_data.forEachIndexed { timeIndex, sessionData ->
                            val showTimeLayout = inflater.inflate(R.layout.row_showtimeindividual, null)
                            val gridLayoutParams = GridLayout.LayoutParams()
                            gridLayoutParams.setMargins(8, 30, 8, 8)
                            showTimeLayout.layoutParams = gridLayoutParams
                            val tvShowTime = showTimeLayout.findViewById<TextView>(R.id.tvShowTime)
                            tvShowTime.text = sessionData.time
                            tvShowTime.setOnClickListener {
                                sessionClicked(expData, sessionData, cid, ccode, cinemaName)
                            }
                            parent.addView(showTimeLayout)
                            LogUtils.d("ShowtimeAdapter", "added  -$index - ${sessionData.time} ")
                        }
                    }
                    LogUtils.d("ShowtimeAdapter", " ${parent.childCount} number of elements ")
                    holder.tblShows.addView(parent)
                }
            }

            1 -> {
                val footerHolder: ViewHolderFooter = holder as ViewHolderFooter
                footerHolder.btnLoadMore.onClick {
                    updateLocalListToCount()
                }
            }
        }
    }

    private fun sessionClicked(expData: ShowtimeResponse.DATa.MovieInfo.Cinemas.ExpData,
                               sessionData: ShowtimeResponse.DATa.MovieInfo.Cinemas.ExpData.SessionData, cid: String, ccode: String, cinemaName: String) {

        AppConstants.putObject(AppConstants.KEY_EXPDATA, expData, context)
        AppConstants.putString(AppConstants.KEY_TIMESTR, sessionData.time_str, context)
        (context as ShowtimeActivity).fetchSeatLayout(
                ccode,cid,sessionData.session_id,sessionData.time_str,movieNameStr,cinemaName,
                expimage,"",imgurl)

    }

    private fun showPopupExperienceInfo(expData: ShowtimeResponse.DATa.MovieInfo.Cinemas.ExpData) {
        val dialog = Dialog(context)
        val mDialogView = LayoutInflater.from(context).inflate(R.layout.expinfo_dialog, null)
        dialog.setContentView(mDialogView);

        dialog.window.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))

        if (expData.exp_desc.isNotBlank()) {
            Picasso.with(context)
                    .load(expData.exp_desc)
                    .into(mDialogView.ivExpPopupImg, object : com.squareup.picasso.Callback {
                        override fun onError() {
                        }

                        override fun onSuccess() {
                            dialog.window.setLayout(matchParent, matchParent)
                            dialog.show();
                        }

                        fun onError(ex: Exception) {
                        }
                    })

        } else {
            context.alert("Image Not Found",
                    context.getString(R.string.marcus_theatre_title)) {
                positiveButton("OK") {
                    it.dismiss()
                }
            }.show()
        }

        mDialogView.ivClosePopup.setOnClickListener {
            dialog.dismiss()
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (localList.get(position).isHeader) {
            return VIEW_FOOTER
        } else {
            return VIEW_DATA
        }
    }

    fun updateLocalListToCount() {
        if (localList.size + 3 <= movieDataPerDate.size) {
            localList.clear()
            listRange += 3
            for (item in 0..listRange) {
                localList.add(movieDataPerDate.get(item))
            }
            val loadMoreItem: ShowtimeResponse.DATa.MovieInfo = ShowtimeResponse.DATa.MovieInfo(null, null, true)
            localList.add(loadMoreItem)
            notifyDataSetChanged()
        } else {
            localList.clear()
            localList = movieDataPerDate
            notifyDataSetChanged()
        }
    }
}

class ViewHolderData(view: View) : RecyclerView.ViewHolder(view) {
    val tvCinemaName = view.findViewById<TextView>(R.id.tvCinemaName)
    val tvMiles = view.findViewById<TextView>(R.id.tvMiles)
    val ivMiles = view.findViewById<ImageView>(R.id.ivLocationMiles)
    val ivPreferedHeart = view.findViewById<ImageView>(R.id.ivPreferred)
    val tblShows = view.findViewById<TableLayout>(R.id.tblShows)
}


class ViewHolderFooter(view: View) : RecyclerView.ViewHolder(view) {
    val btnLoadMore = view.findViewById<Button>(R.id.btnViewMore)
}