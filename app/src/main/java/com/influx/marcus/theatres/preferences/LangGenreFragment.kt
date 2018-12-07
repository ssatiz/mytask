package com.influx.marcus.theatres.preferences


import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.preferedlocations.PreferedLocsResp
import com.influx.marcus.theatres.api.ApiModels.preferedlocations.genredata
import com.influx.marcus.theatres.api.ApiModels.preferedlocations.langdata
import com.influx.marcus.theatres.utils.AppConstants
import org.apmem.tools.layouts.FlowLayout


class LangGenreFragment : Fragment() {

    companion object {
        private var prefData: PreferedLocsResp? = null
        fun newInstance(resp: PreferedLocsResp) = LangGenreFragment().apply {
            prefData = resp
        }

    }

    lateinit var mcontext: Context
    var font: Typeface? = null
    val tvSize = 12
    private lateinit var tvLayoutParams: LinearLayout.LayoutParams
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var rootView: View = inflater.inflate(R.layout.fragment_lang_genre, container, false)
        mcontext = this!!.activity!!
        font = ResourcesCompat.getFont(mcontext, R.font.gotham_book)
        val genreFlowLayout: FlowLayout = rootView.findViewById(R.id.flGenre)
        val languageFlowLayout: FlowLayout = rootView.findViewById(R.id.flLanguage)
        tvLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
        tvLayoutParams.gravity = Gravity.CENTER
        genreFlowLayout.removeAllViews()
        languageFlowLayout.removeAllViews()
        if (prefData != null) {
            populateGenreLayoutChilds(genreFlowLayout)
            populateLanguageLayoutChilds(languageFlowLayout)
        }

        return rootView
    }

    private fun populateGenreLayoutChilds(genreFlowLayout: FlowLayout) {

        prefData!!.DATA!!.genres!!.forEachIndexed { i, eachGenre ->
            val parent = LinearLayout(activity)
            val parLP = FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT,
                    FlowLayout.LayoutParams.WRAP_CONTENT)
            parent.layoutParams = parLP
            parLP.setMargins(12, 12, 12, 12)
            parLP.gravity = Gravity.CENTER_VERTICAL
            parent.orientation = LinearLayout.HORIZONTAL
            parent.setPadding(20, 30, 20, 30)
            parent.setBackgroundResource(R.drawable.black_prefbackground)


            val tvGenre = buildLabel(eachGenre)
            tvGenre.setTextColor(Color.WHITE)
            tvGenre.setTypeface(font)
            tvGenre.setTextSize(tvSize.toFloat())
            tvGenre.setSingleLine(true)
            tvGenre.setId(i + 1)

            val ivFav = ImageView(activity)
            val ivLP = LinearLayout.LayoutParams(50, 50)
            ivFav.layoutParams = ivLP
            ivLP.setMargins(16, 0, 0, 0)
            ivLP.gravity = Gravity.CENTER
            ivFav.setImageResource(R.drawable.fav_un)
            val p = genredata(prefData!!.DATA!!.genres!!.get(i).toString(),
                    false)
            parent.setTag(p)
            AppConstants.genreList.forEachIndexed { index, s ->
                if (s.equals(eachGenre, ignoreCase = true)) {
                    parent.setBackgroundResource(R.drawable.red_prefbackground)
                    ivFav.setImageResource(R.drawable.fav)
                    val p = genredata(prefData!!.DATA!!.genres!!.get(i).toString(),
                            true)
                    parent.setTag(p)
                }
            }

            parent.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    //v.getTag()
                    val data = v!!.tag as genredata
                    if (data.favourite == false) {
                        ivFav.setImageResource(R.drawable.fav)
                        parent.setBackgroundResource(R.drawable.red_prefbackground)
                        data.favourite = true
                        if (data.name in AppConstants.genreList) {
                            // do nothing
                        } else {
                            AppConstants.genreList.add(data.name)
                        }
                    } else {
                        ivFav.setImageResource(R.drawable.fav_un)
                        parent.setBackgroundResource(R.drawable.black_prefbackground)
                        data.favourite = false
                        if (data.name in AppConstants.genreList) {
                            AppConstants.genreList.remove(data.name)
                        }//else do nothing
                    }
                }
            })
            parent.addView(tvGenre)
            parent.addView(ivFav)
            genreFlowLayout.addView(parent)
        }
    }

    private fun populateLanguageLayoutChilds(languageFlowLayout: FlowLayout) {


        prefData!!.DATA!!.languages!!.forEachIndexed { i, eachLang ->

            val parent = LinearLayout(activity)
            val parLP = FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT,
                    FlowLayout.LayoutParams.WRAP_CONTENT)
            parent.layoutParams = parLP
            parLP.setMargins(12, 12, 12, 12)
            parent.orientation = LinearLayout.HORIZONTAL
            parent.setPadding(20, 30, 20, 30)
            parent.setBackgroundResource(R.drawable.black_prefbackground)

            val tvLang = buildLabel(eachLang)
            tvLang.setTextColor(Color.WHITE)
            tvLang.setTypeface(font)
            tvLang.setTextSize(tvSize.toFloat())
            tvLang.setSingleLine(true)
            tvLang.setId(i + 1)


            val ivFav = ImageView(activity)
            val ivLP = LinearLayout.LayoutParams(50, 50)
            ivFav.layoutParams = ivLP
            ivLP.setMargins(16, 0, 0, 0)
            ivLP.gravity = Gravity.CENTER
            ivFav.setImageResource(R.drawable.fav_un)
            val p = langdata(prefData!!.DATA!!.languages!!.get(i),
                    false)
            parent.setTag(p)
            AppConstants.languageList.forEachIndexed { index, s ->
                if (s.equals(eachLang, ignoreCase = true)) {
                    parent.setBackgroundResource(R.drawable.red_prefbackground)
                    ivFav.setImageResource(R.drawable.fav)
                    val p = langdata(prefData!!.DATA!!.languages!!.get(i),
                            true)
                    parent.setTag(p)
                }
            }
            parent.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    //v.getTag()
                    val data = v!!.tag as langdata
                    if (data.favourite == false) {
                        ivFav.setImageResource(R.drawable.fav)
                        parent.setBackgroundResource(R.drawable.red_prefbackground)
                        data.favourite = true
                        if (data.name in AppConstants.languageList) {
                            //do nothing
                        } else {
                            //todo why you are checking number over here ??
                            if (data.name.matches("-?\\d+(\\.\\d+)?".toRegex())) {
                                Log.i("string contains number", data.name)
                            } else {
                                Log.i("language list", data.name)
                                AppConstants.languageList.add(data.name)
                            }
                        }
                    } else {
                        ivFav.setImageResource(R.drawable.fav_un)
                        parent.setBackgroundResource(R.drawable.black_prefbackground)
                        data.favourite = false
                        if (data.name in AppConstants.languageList) {
                            AppConstants.languageList.remove(data.name)
                        }
                    }
                }
            })
            parent.addView(tvLang)
            parent.addView(ivFav)
            languageFlowLayout.addView(parent)
        }
    }

    private fun buildLabel(text: String): TextView {
        val textView = TextView(mcontext)
        textView.layoutParams = tvLayoutParams
        textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        textView.gravity = Gravity.CENTER
        textView.text = text.toUpperCase()
        textView.setTextColor(Color.WHITE)
        textView.setSingleLine(true)
        return textView
    }


}
