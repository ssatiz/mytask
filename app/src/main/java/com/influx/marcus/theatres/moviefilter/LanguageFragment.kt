package com.influx.marcus.theatres.moviefilter

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
import android.widget.LinearLayout
import android.widget.TextView
import com.influx.marcus.theatres.R
import com.influx.marcus.theatres.api.ApiModels.preferedlocations.genredata
import com.influx.marcus.theatres.utils.AppConstants
import com.influx.marcus.theatres.utils.LogUtils
import org.apmem.tools.layouts.FlowLayout

class LanguageFragment : Fragment(), clearLanguage {

    lateinit var uiHelper: filterUiHelper
    lateinit var genreFlowLayout: FlowLayout

    companion object {
        lateinit var language: ArrayList<String>
        fun newInstance(resp: ArrayList<String>, uiListener: filterUiHelper) = LanguageFragment().apply {
            language = resp
            uiHelper = uiListener
        }

        private lateinit var tvLayoutParams: LinearLayout.LayoutParams
        lateinit var mcontext: Context
        val tvSize = 12
        var font: Typeface? = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var rootView: View = inflater.inflate(R.layout.fragment_language, container, false)
        mcontext = (this.activity as Context?)!!
        genreFlowLayout = rootView.findViewById(R.id.flLanguage)
        tvLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
        tvLayoutParams.gravity = Gravity.CENTER
        clearData()

        return rootView
    }

    private fun populateLanguageLayoutChilds(languageFlowLayout: FlowLayout) {


        language!!.forEachIndexed { i, eachLang ->

            val parent = LinearLayout(activity)
            val parLP = FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT,
                    FlowLayout.LayoutParams.WRAP_CONTENT)
            parent.layoutParams = parLP
            parLP.setMargins(12, 12, 12, 12)
            parent.orientation = LinearLayout.HORIZONTAL
            parent.setPadding(20, 30, 20, 30)
            parent.setBackgroundResource(R.drawable.black_prefbackground)

            font = ResourcesCompat.getFont(mcontext, R.font.gotham_book);
            val tvLang = buildLabel(eachLang)
            tvLang.setTextColor(Color.WHITE)
            tvLang.setTypeface(font)
            tvLang.setTextSize(tvSize.toFloat())
            tvLang.setSingleLine(true)
            tvLang.setId(i + 1)


            val ivLP = LinearLayout.LayoutParams(50, 50)
            ivLP.setMargins(16, 0, 0, 0)
            ivLP.gravity = Gravity.CENTER



            val p = genredata(language!!.get(i).toString(),
                    false)
            parent.setTag(p)
            AppConstants.selectedLanguagestoFilter.forEachIndexed { index, s ->
                if (s.equals(eachLang, ignoreCase = true)) {
                    val p = genredata(language!!.get(i).toString(),
                            true)
                    parent.setTag(p)
                    parent.setBackgroundResource(R.drawable.red_prefbackground)
                    uiHelper.enableResetButton()
                    uiHelper.enableSavebutton()

                }
            }
            parent.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    //v.getTag()
                    val data = v!!.tag as genredata
                    if (data.favourite == false) {
                        parent.setBackgroundResource(R.drawable.red_prefbackground)
                        data.favourite = true

                        AppConstants.selectedLanguagestoFilter.add(language.get(i))
                        AppConstants.putStringList(AppConstants.KEY_FILTERLANGUAGE,AppConstants.selectedLanguagestoFilter, GenreFragment.mcontext)
                        uiHelper.enableSavebutton()
                        uiHelper.enableResetButton()
                        LogUtils.d("selectedlanguagelist", AppConstants.selectedLanguagestoFilter.toString())
                        if (data.name in AppConstants.languageList) {
                            //do nothing
                        } else {
                            if (data.name.matches("-?\\d+(\\.\\d+)?".toRegex())) {
                                Log.i("string contains number", data.name)
                            } else {
                                Log.i("language list", data.name)
                                AppConstants.languageList.add(data.name)
                            }
                        }
                    } else {
                        parent.setBackgroundResource(R.drawable.black_prefbackground)
                        data.favourite = false
                        AppConstants.selectedLanguagestoFilter.remove(language.get(i))
                        if (AppConstants.selectedLanguagestoFilter.size == 0) {
                            uiHelper.disableSaveButton()
                            uiHelper.disableResetButton()
                        }
                        LogUtils.d("selectedlanguagelist", AppConstants.selectedLanguagestoFilter.toString())
                        AppConstants.putStringList(AppConstants.KEY_FILTERLANGUAGE,AppConstants.selectedLanguagestoFilter, mcontext)

                        if (data.name in AppConstants.languageList) {
                            AppConstants.languageList.remove(data.name)
                        }
                    }
                }
            })
            parent.addView(tvLang)
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

    override fun clearData() {
        genreFlowLayout.removeAllViews()
        populateLanguageLayoutChilds(genreFlowLayout)
    }
}

interface clearLanguage {
    fun clearData()
}
