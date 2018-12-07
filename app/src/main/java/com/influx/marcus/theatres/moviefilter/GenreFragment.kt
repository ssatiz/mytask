package com.influx.marcus.theatres.moviefilter

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
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

class GenreFragment : Fragment(), clearGenre {


    private lateinit var uiHelper: filterUiHelper
    lateinit var genreFlowLayout: FlowLayout

    companion object {
        lateinit var genre: ArrayList<String>
        fun newInstance(resp: ArrayList<String>, uiListener: filterUiHelper) = GenreFragment().apply {
            uiHelper = uiListener
            genre = resp
        }

        private lateinit var tvLayoutParams: LinearLayout.LayoutParams
        lateinit var mcontext: Context
        val tvSize = 12
        var font: Typeface? = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var rootView: View = inflater.inflate(R.layout.fragment_genre, container, false)
        mcontext = (this.activity as Context?)!!

        genreFlowLayout = rootView.findViewById(R.id.flGenre)
        tvLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
        tvLayoutParams.gravity = Gravity.CENTER
        clearData()
        return rootView
    }

    private fun populateGenreLayoutChilds(genreFlowLayout: FlowLayout) {

        genre.forEachIndexed { i, eachGenre ->
            val parent = LinearLayout(activity)
            val parLP = FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT,
                    FlowLayout.LayoutParams.WRAP_CONTENT)
            parent.layoutParams = parLP
            parLP.setMargins(12, 12, 12, 12)
            parLP.gravity = Gravity.CENTER_VERTICAL
            parent.orientation = LinearLayout.HORIZONTAL
            parent.setPadding(20, 30, 20, 30)
            parent.setBackgroundResource(R.drawable.black_prefbackground)

            font = ResourcesCompat.getFont(mcontext, R.font.gotham_book);
            val tvGenre = buildLabel(eachGenre)
            tvGenre.setTextColor(Color.WHITE)
            tvGenre.typeface = font
            tvGenre.setTextSize(tvSize.toFloat())
            tvGenre.setSingleLine(true)
            tvGenre.setId(i + 1)

            val ivLP = LinearLayout.LayoutParams(50, 50)
            ivLP.setMargins(16, 0, 0, 0)
            ivLP.gravity = Gravity.CENTER



            val p = genredata(genre.get(i).toString(), false)
            parent.setTag(p)
            AppConstants.selectedGenrestoFilter.forEachIndexed { index, s ->
                if (s.equals(eachGenre, ignoreCase = true)) {
                    val p = genredata(genre!!.get(i).toString(),
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
                        AppConstants.selectedGenrestoFilter.add(genre.get(i))
                        AppConstants.putStringList(AppConstants.KEY_FILTERGENRE, AppConstants.selectedGenrestoFilter, mcontext)
                        uiHelper.enableSavebutton()
                        uiHelper.enableResetButton()

                        LogUtils.d("selectedgenrelist", AppConstants.selectedGenrestoFilter.toString())
                        if (data.name in AppConstants.genreList) {
                            // do nothing
                        } else {
                            AppConstants.genreList.add(data.name)
                        }
                    } else {
                        parent.setBackgroundResource(R.drawable.black_prefbackground)
                        data.favourite = false
                        AppConstants.selectedGenrestoFilter.remove(genre.get(i))
                        if (AppConstants.selectedGenrestoFilter.size == 0) {
                            uiHelper.disableSaveButton()
                            uiHelper.disableResetButton()
                        }
                        AppConstants.putStringList(AppConstants.KEY_FILTERGENRE, AppConstants.selectedGenrestoFilter, mcontext)
                        LogUtils.d("selectedgenrelist", AppConstants.selectedGenrestoFilter.toString())
                        if (data.name in AppConstants.genreList) {
                            AppConstants.genreList.remove(data.name)
                        }//else do nothing
                    }
                }
            })
            parent.addView(tvGenre)
            genreFlowLayout.addView(parent)
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
        populateGenreLayoutChilds(genreFlowLayout)
    }
}

interface clearGenre {
    fun clearData()
}