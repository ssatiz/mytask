package com.influx.marcus.theatres.test


import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.influx.marcus.theatres.R
import kotlinx.android.synthetic.main.payment_layout.*

class TestPay : AppCompatActivity(), View.OnClickListener {
    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.tvGiftCard -> {

                if(llgiftCardDetail.visibility==View.VISIBLE){
                    llgiftCardDetail.setVisibility(View.GONE)

                }else
                llgiftCardDetail.visibility = View.VISIBLE
            }

        }
    }


    lateinit var llgiftCardDetail: LinearLayout


    private lateinit var tvGiftCard: RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.payment_layout)
        llgiftCardDetail = findViewById(R.id.llgiftCardDetail)
        tvGiftCard = findViewById(R.id.tvGiftCard)
        // llMagical.visibility=View.VISIBLE
          llGiftCard.visibility = View.VISIBLE
         llSavedCards.visibility = View.VISIBLE

        tvGiftCard.setOnClickListener(this)
    }


}