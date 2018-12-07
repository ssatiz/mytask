package com.influx.marcus.theatres.payment

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.influx.marcus.theatres.R

/**
 * Created by Kavitha on 02-04-2018.
 */
class CardViewPagerAdapter(var context: Context) : PagerAdapter() {

    private var views = SparseArray<View>()
    lateinit var inflater: LayoutInflater

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView: View = inflater.inflate(R.layout.adapter_card, container, false)
        try {


        } catch (e: Exception) {
            e.printStackTrace()
        }
        container?.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val view: View? = `object` as View

        (container as ViewPager).removeView(view)
        views.remove(position)
    }

    override fun getCount(): Int {
        return 2
    }
}