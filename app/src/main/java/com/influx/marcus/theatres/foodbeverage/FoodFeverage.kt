package com.influx.marcus.theatres.foodbeverage

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.astuetz.PagerSlidingTabStrip
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.cinegreetings.Cinegreeting
import com.influx.marcus.theatres.common.AppStorage
import com.influx.marcus.theatres.commonview.UtilDialog
import com.influx.marcus.theatres.showtime.ShowtimeActivity
import kotlinx.android.synthetic.main.tab_layout.view.*
import org.jetbrains.anko.alert


class FoodFeverage : AppCompatActivity() {
    lateinit var viewPager: ViewPager
    lateinit var foodModel: FoodModel
    lateinit var foodList: FoodList
    lateinit var tv: TextView
    lateinit var tabLayout: PagerSlidingTabStrip
    lateinit var back: ImageView
    lateinit var txtPay: TextView
    lateinit var txtSkip: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_feverage)
        AppStorage.putBoolean(AppStorage.ISFOODADD, false, this@FoodFeverage)
        initialize()
    }

    private fun initialize() {
        UtilDialog.showProgressDialog(this@FoodFeverage, "")
        viewPager = findViewById(R.id.viewpager)
        tabLayout = findViewById<PagerSlidingTabStrip>(R.id.tabs)
        back = findViewById(R.id.ivBack)
        txtPay = findViewById(R.id.txtPayy)
        txtSkip = findViewById(R.id.tvTimer)
        foodList = FoodList()
        getFoodList()

        setupViewPager(viewPager, foodList.getFoodList())
        tabLayout.setViewPager(viewPager)
        tabSelect(0)

        back.setOnClickListener({
            back()
        })
        txtPay.setOnClickListener({ navigateToCine() })
        txtSkip.setOnClickListener({
            var intet = Intent(this@FoodFeverage, Cinegreeting::class.java)
            startActivity(intet)
        })

        Handler().postDelayed({
            UtilDialog.hideProgress()
        }, 3000)
    }

    private fun getFoodList() {
        foodModel = FoodModel("Combo1", "0", "https://s31.postimg.cc/rgikh58ln/Layer_9_1_1.png", "15", "Nonveg")
        //  foodModel = FoodModel("purger", "0", "ee", "50", "Veg")
        var combolist = ArrayList<FoodModel>()
        combolist.add(foodModel)
        foodModel = FoodModel("Combo2", "0", "https://s31.postimg.cc/9qgvvwse3/Layer_18_1.png", "20", "Veg")
        combolist.add(foodModel)
        foodModel = FoodModel("Combo3", "0", "https://s31.postimg.cc/wdmyb3vwr/Layer_19_1_1.png", "35", "Veg")
        combolist.add(foodModel)
        foodModel = FoodModel("Combo4", "0", "https://s31.postimg.cc/rgikh58ln/Layer_9_1_1.png", "45", "Nonveg")
        combolist.add(foodModel)

        var foodCat = FoodCategoryList()

        foodCat.setPopcornlist(combolist)
        foodCat.setCombolist(combolist)
        foodCat.setRecommanedlist(combolist)
        // var arrFoodCat = FoodList()
        foodList.setFoodList(foodCat)
    }

    fun back() {
        startActivity(Intent(this@FoodFeverage, ShowtimeActivity::class.java))
        finish()
    }

    fun navigateToCine() {
        if (AppStorage.getBoolean(AppStorage.ISFOODADD, this@FoodFeverage)) {
            var intet = Intent(this@FoodFeverage, Cinegreeting::class.java)
            startActivity(intet)
        } else {
            alert( "Please select any one item to proceed or skip",
                    getString(R.string.marcus_theatre_title)) {
                positiveButton("OK") {
                    it.dismiss()
                }
            }.show()

        }
    }

    fun setupViewPager(viewPager: ViewPager, foodModel: FoodCategoryList) {
        val tabsList = ArrayList<String>()
        tabsList.add(resources.getString(R.string.recommanded))
        tabsList.add(resources.getString(R.string.combo))
        tabsList.add(resources.getString(R.string.popcorn))


        var adapter = ViewPagerAdapter(getSupportFragmentManager(), tabsList, foodModel)
        viewPager.adapter = adapter

        tabLayout.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {

                tabSelect(position)


            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })


    }

    fun tabSelect(position: Int) {
        val mTabsLinearLayout = tabLayout.getChildAt(0) as LinearLayout

        for (i in 0 until mTabsLinearLayout.childCount) {
            tv = mTabsLinearLayout.getChildAt(i).tab_title
            if (i == position) {
                tv.setTextColor(ContextCompat.getColor(this@FoodFeverage, R.color.marcus_red))
                val typeface = ResourcesCompat.getFont(this, R.font.gothambold)
                tv.setTypeface(typeface)
            } else {
                tv.setTextColor(Color.WHITE)
                val typeface = ResourcesCompat.getFont(this, R.font.gothambook)
                tv.setTypeface(typeface)
            }
        }
    }


}
