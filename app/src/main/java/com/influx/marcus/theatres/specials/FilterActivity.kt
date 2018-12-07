package com.influx.marcus.theatres.specials

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.specials.DATA
import com.influx.marcus.theatres.api.ApiModels.specials.PromotionType
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.LogUtils
import kotlinx.android.synthetic.main.activity_filter.*
import org.apmem.tools.layouts.FlowLayout
import java.util.*

class FilterActivity : AppCompatActivity() {
    lateinit var promotionTypes: DATA
    private lateinit var tvLayoutParams: LinearLayout.LayoutParams
    lateinit var selectedPromotionlist: ArrayList<String>
    val tvSize = 12
    var font: Typeface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)
        selectedPromotionlist = AppConstants.promotion
        initViews()
    }

    private fun initViews() {
        tvLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
        tvLayoutParams.gravity = Gravity.CENTER
        promotionTypes = AppConstants.getObject(AppConstants.KEY_PROMOTIONS_DATA,
                this@FilterActivity, DATA::class.java as Class<Any>) as DATA

        ivSave.setOnClickListener {
            AppConstants.putStringList(AppConstants.KEY_PROMOTIONS, AppConstants.promotion,
                    this@FilterActivity)
            LogUtils.d("KEY_PROMOTIONS", AppConstants.getStringList(AppConstants.KEY_PROMOTIONS,
                    this@FilterActivity).toString())

            val i = Intent(this@FilterActivity, SpecialsActivity::class.java)
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(i)
            finish()
            overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
        }
        ivReset.setOnClickListener {
            flPromotion.removeAllViews()
            populatePromotionLayoutChilds(flPromotion, true)
            selectedPromotionlist.clear()
            AppConstants.promotion.clear()
            val emptyList = ArrayList<String>()
            AppConstants.putStringList(AppConstants.KEY_PROMOTIONS,
                    emptyList, this@FilterActivity)
        }
        ivBack.setOnClickListener {
            onBackPressed()
        }
        populatePromotionLayoutChilds(flPromotion, false)
    }

    private fun populatePromotionLayoutChilds(genreFlowLayout: FlowLayout, isRefresh: Boolean) {

        promotionTypes.promotion_types.forEachIndexed { i, eachPromotion ->
            val parent = LinearLayout(this@FilterActivity)
            val parLP = FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT,
                    FlowLayout.LayoutParams.WRAP_CONTENT)
            parent.layoutParams = parLP
            parLP.setMargins(12, 12, 12, 12)
            parLP.gravity = Gravity.CENTER_VERTICAL
            parent.orientation = LinearLayout.HORIZONTAL
            parent.setPadding(20, 30, 20, 30)
            parent.setBackgroundResource(R.drawable.black_prefbackground)
            font = ResourcesCompat.getFont(this, R.font.gotham_book);
            val tvPromotion = buildLabel(eachPromotion.title)
            tvPromotion.setTextColor(Color.WHITE)
            tvPromotion.setTypeface(font)
            tvPromotion.setTextSize(tvSize.toFloat())
            tvPromotion.setSingleLine(true)
            tvPromotion.setId(i + 1)
            val specialPromotion = AppConstants.getStringList(AppConstants.KEY_PROMOTIONS, this)
            if (isRefresh == false) {
                for (eachpromotion in promotionTypes.promotion_types)
                    if (eachPromotion.title in specialPromotion)
                        parent.setBackgroundResource(R.drawable.red_prefbackground)
                LogUtils.d("KEY_PROMOTIONS", AppConstants.getStringList(AppConstants.KEY_PROMOTIONS,
                        this@FilterActivity).toString())
/*
                AppConstants.getStringList(AppConstants.KEY_PROMOTIONS,
                   this).forEachIndexed { index, s ->
                if (s.equals(eachPromotion.title, ignoreCase = true)) {
                    parent.setBackgroundResource(R.drawable.red_prefbackground)
                }}*/
            }


            val p = PromotionType(promotionTypes.promotion_types.get(i).title,
                    false)
            parent.setTag(p)
            parent.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    //v.getTag()
                    val data = v!!.tag as PromotionType
                    if (data.favourite == false) {
                        parent.setBackgroundResource(R.drawable.red_prefbackground)
                        data.favourite = true
                        selectedPromotionlist.add(promotionTypes.promotion_types.get(i).title)
                        LogUtils.d("selectedgenrelist", selectedPromotionlist.toString())
                        if (data.title in AppConstants.promotion) {
                            // do nothing
                        } else {
                            AppConstants.promotion.add(data.title)
                        }
                    } else {
                        parent.setBackgroundResource(R.drawable.black_prefbackground)
                        data.favourite = false
                        selectedPromotionlist.remove(promotionTypes.promotion_types.get(i).title)
                        LogUtils.d("selectedgenrelist", selectedPromotionlist.toString())
                        if (data.title in AppConstants.promotion) {
                            AppConstants.promotion.remove(data.title)
                        }//else do nothing
                    }
                }
            })
            parent.addView(tvPromotion)
            genreFlowLayout.addView(parent)
        }
    }

    private fun buildLabel(text: String): TextView {
        val textView = TextView(this)
        textView.layoutParams = tvLayoutParams
        textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        textView.gravity = Gravity.CENTER
        textView.text = text.toUpperCase()
        textView.setTextColor(Color.WHITE)
        textView.setSingleLine(true)
        return textView
    }

    override fun onResume() {
        super.onResume()
    }


    override fun onPause() {
        super.onPause()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.animator.slide_from_left , R.animator.slide_to_right)
    }
}
