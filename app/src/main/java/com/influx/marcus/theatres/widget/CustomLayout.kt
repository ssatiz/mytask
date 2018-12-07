package com.influx.marcus.theatres.widget


import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.seatlayout.Rowlist
import com.influx.marcus.theatres.api.ApiModels.seatlayout.SeatReq
import com.influx.marcus.theatres.api.ApiModels.seatlayout.SeatResp
import com.influx.marcus.theatres.api.ApiModels.seatlayout.Seatlist
import com.influx.marcus.theatres.api.ApiModels.showtime.ShowtimeResponse
import com.influx.marcus.theatres.api.RestClient
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.LogUtils
import com.influx.marcus.theatres.utils.UtilsDialog
import com.otaliastudios.zoom.ZoomLayout
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.textColor
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class CustomLayout : LinearLayout {

    private var mContext: Context? = null
    private var viewHeight: Int = 0
    private var viewWidth: Int = 0
    private var mParentView: LinearLayout? = null
    private var rowNum: Int = 0
    private var verticalViewWidth: Int = 0
    var childClickListener: CustomChildClickListener? = null
    private val mPlayList = ArrayList<ShowtimeResponse.DATa.MovieInfo.Cinemas.ExpData.SessionData>()

    interface CustomChildClickListener {
        fun onPreviewClicked()
        fun onBuyTicketClicked()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView(context)
    }

    private fun initView(context: Context) {
        ivLp = TableRow.LayoutParams(resources.getDimension(R.dimen._10sdp).toInt(),
                resources.getDimension(R.dimen._10sdp).toInt())
        ivLp.setMargins(4, 4, 4, 4)
        this.mContext = context
        verticalViewWidth = AnimUtil.dip2px(mContext!!, 1f)
        val root = View.inflate(mContext, R.layout.popup_seatpreview, null)

        val widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        root.measure(widthSpec, heightSpec)
        viewHeight = root.measuredHeight
        viewWidth = (mContext!!.resources.displayMetrics.widthPixels - AnimUtil.dip2px(mContext!!, 2f)) /
                ExpandableGridView.CHILD_COLUMNUM
    }

    constructor(context: Context) : super(context) {
        initView(context)
    }

    var ccode: String? = null
    var ccid: String? = null

    fun refreshDataSet(playList: ShowtimeResponse.DATa.MovieInfo.Cinemas.ExpData.SessionData, ccode: String, ccid: String) {
        mPlayList.clear()
        mPlayList.add(playList)
        this.ccode = ccode
        this.ccid = ccid
//        mPlayList.addAll(playList)
        notifyDataSetChange(false)
    }

    fun notifyDataSetChange(needAnim: Boolean) {
        removeAllViews()
        rowNum = mPlayList.size / ExpandableGridView.COLUMNUM +
                if (mPlayList.size % ExpandableGridView.COLUMNUM > 0) 1 else 0
        val rowParam =
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val verticalParams = LinearLayout.LayoutParams(verticalViewWidth, LinearLayout.LayoutParams.FILL_PARENT)
        val horizontalParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, verticalViewWidth)
        for (rowIndex in 0 until rowNum) {
            val llContainer = LinearLayout(mContext)
            llContainer.orientation = LinearLayout.HORIZONTAL
            val itemParam = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            )
            itemParam.width = viewWidth
            for (columnIndex in 0 until ExpandableGridView.COLUMNUM) {
                val itemInfoIndex = rowIndex * ExpandableGridView.COLUMNUM + columnIndex
                var isValidateView = true
                if (itemInfoIndex >= mPlayList.size) {
                    isValidateView = false
                }
                val root = View.inflate(mContext, R.layout.popup_seatpreview, null)
                val previewButton = root.findViewById<View>(R.id.btnOpenPanel) as RelativeLayout
                val buyTicketsButton = root.findViewById<View>(R.id.btnBuyTickets) as Button
                val superChild = root.findViewById<View>(R.id.llSeatPreviewLayout) as LinearLayout

                enablePreview(previewButton)
                if (isValidateView) {

                    previewButton.setOnClickListener {
                        if (childClickListener != null) {
                            showPreviewView(root, superChild)
                            disablePreview(previewButton)
                        }
                    }
                    buyTicketsButton.setOnClickListener {
                        if (childClickListener != null) {
                            childClickListener!!.onBuyTicketClicked()
                        }
                    }

                }
                llContainer.addView(root, itemParam)
            }
            addView(llContainer, rowParam)
            Log.e("animator", "" + height + "--" + rowNum * viewHeight)
            if (needAnim) {
                createHeightAnimator(mParentView, height, rowNum * viewHeight)
            }
        }
    }


    fun createHeightAnimator(view: View?, start: Int, end: Int) {
        val animator = ValueAnimator.ofInt(start, end)
        animator.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int

            val layoutParams = view!!.layoutParams
            layoutParams.height = value
            view.layoutParams = layoutParams
        }
        animator.duration = 200
        animator.start()
    }

    fun createChildHeightAnimator(view: View?, start: Int, end: Int) {
        val animator = ValueAnimator.ofInt(start, end)
        animator.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int

            val layoutParams = view!!.layoutParams
            layoutParams.height = value
            view.layoutParams = layoutParams
        }
        animator.duration = 200
        animator.start()
    }


    fun setParentView(llBtm: LinearLayout) {
        this.mParentView = llBtm
    }

    fun showPreviewView(root: View, superChild: LinearLayout) {
        superChild.visibility = View.VISIBLE
        Log.e("Child animator", "" + height + "--" + (root.measuredHeight * 2))
        Log.e("Child animator2", "" + (height+150) + "--" + (root.measuredHeight * 6))
        Log.e("Child animator3", "" + height + "--" + (viewHeight * 6.5).toInt())



        createChildHeightAnimator(mParentView, height, ((viewHeight * 6.5).toInt()))
        val req = SeatReq(ccode!!, ccid!!, mPlayList.get(0).session_id, mPlayList.get(0).time_str)
        val seatCall: Call<SeatResp> = webApi.getSeatLayout(
                AppConstants.AUTHORISATION_HEADER,
                req
        )
        if (UtilsDialog.isNetworkStatusAvialable(context)) {
            UtilsDialog.showProgressDialog(context, "")
        }

        seatCall.enqueue(object : Callback<SeatResp> {
            override fun onFailure(call: Call<SeatResp>?, t: Throwable?) {
                Toast.makeText(context, "Server Error.",
                        Toast.LENGTH_SHORT).show()
                UtilsDialog.hideProgress()
            }

            override fun onResponse(call: Call<SeatResp>?, response: Response<SeatResp>?) {
                if (response!!.isSuccessful) {
                    UtilsDialog.hideProgress()
                    val seatResp = response.body()
                    if (seatResp!!.STATUS == true) {
                        try {
                            val rowList = seatResp!!.DATA.arealist.rowlist as ArrayList<Rowlist>
                            formTablelayout(rowList, superChild.findViewById(R.id.tblSeats),
                                    superChild.findViewById(R.id.zlParent))
                        } catch (e: Exception) {
                            LogUtils.d("SEATPREIVEW-Exception", e.message!!)
                            e.printStackTrace()
                        }
                    } else {
                        UtilsDialog.hideProgress()
                        Toast.makeText(context, seatResp!!.DATA.message,
                                Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private val webApi = RestClient.getApiClient()

    private lateinit var ivLp: TableRow.LayoutParams

    private fun formTablelayout(rowList: ArrayList<Rowlist>, tblSeats: TableLayout, zlParent: ZoomLayout) {
        tblSeats.removeAllViews()
        doAsync {

            var rowCount = 0
            val lp = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT)
            lp.setMargins(4, 0, 4, 0)

            for (row in rowList) {
                val tableRow = TableRow(context)
                tableRow.gravity = Gravity.CENTER_HORIZONTAL
                tableRow.layoutParams = lp

                // generateEmpty method makes new empty tv to be added
                tableRow.addView(generateEmptySpaceTV())
                val tvRowNameStart = getRowNameTextView(lp, row.PhysicalName)
                tableRow.addView(tvRowNameStart)
                tableRow.addView(generateEmptySpaceTV())

                for (eachSeat in row.seatlist) {
                    val ivSeat = TextView(context)
                    ivSeat.layoutParams = ivLp
                    ivSeat.gravity = Gravity.CENTER
                    setupSeatBackground(eachSeat, ivSeat)
                    eachSeat.isSelect = false
                    tableRow.addView(ivSeat)
                }

                var tvRowNameEnd = TextView(context)
                tvRowNameEnd.layoutParams = lp
                tvRowNameEnd.textSize = 11f
                tvRowNameEnd.gravity = Gravity.CENTER
                tvRowNameEnd.text = row.PhysicalName
                tvRowNameEnd.textColor = resources.getColor(R.color.white)
                tableRow.addView(generateEmptySpaceTV())
                tableRow.addView(tvRowNameEnd)
                tableRow.addView(generateEmptySpaceTV())
                uiThread {
                    tblSeats.addView(tableRow, rowCount)
                    rowCount++
                }
            }
        }
//        dismissDialogAfterOneSec(zlParent)
    }

    fun getRowNameTextView(tvSeatLp: TableRow.LayoutParams, rowName: String): TextView {
        var tvRowNameStart = TextView(context)
        tvRowNameStart.textSize = 11f
        tvRowNameStart.layoutParams = tvSeatLp
        tvRowNameStart.setPadding(0, 0, 0, 2)
        tvRowNameStart.text = rowName
        tvRowNameStart.gravity = Gravity.CENTER
        tvRowNameStart.textColor = resources.getColor(R.color.white)
        return tvRowNameStart
    }

    private fun setupSeatBackground(eachSeat: Seatlist, ivSeat: TextView) {
        if (eachSeat.Status.equals("Available", ignoreCase = true)) {
            when (eachSeat.SeatStyle.toLowerCase()) {
                "normal" -> { //space
                    ivSeat.setBackgroundResource((R.drawable.empty_seat_icon))
                }
                "loveseatright" -> {// unselected
                    ivSeat.setBackgroundResource((R.drawable.empty_seat_icon))
                }
                "loveseatleft" -> {//selecte
                    ivSeat.setBackgroundResource((R.drawable.empty_seat_icon))
                }
                "companion" -> {//occupied
                    ivSeat.setBackgroundResource((R.drawable.companion_seat_icon))
                }
                "wheelchair" -> {//companion
                    ivSeat.setBackgroundResource((R.drawable.wheelchair_seat_icon))
                }
                "sold" -> {
                    ivSeat.setBackgroundResource((R.drawable.selected_seat_grey))
                }
                "available" -> {
                    ivSeat.setBackgroundResource((R.drawable.empty_seat_icon))
                }
                else -> {
                    ivSeat.setBackgroundResource((R.drawable.selected_seat_grey))
                }
            }
        } else if (eachSeat.Status.equals("sold", ignoreCase = true)) {
            ivSeat.setBackgroundResource((R.drawable.selected_seat_grey))
        } else {
            ivSeat.setBackgroundResource((R.drawable.black_filled))
        }
    }

    fun generateEmptySpaceTV(): TextView {
        val tvEmptySpace = TextView(context)
        tvEmptySpace.layoutParams = ivLp
        return tvEmptySpace
    }


    fun disablePreview(previewButton: RelativeLayout) {
        previewButton.isEnabled = false
    }

    fun enablePreview(previewButton: RelativeLayout) {
        previewButton.isEnabled = true
    }

}

