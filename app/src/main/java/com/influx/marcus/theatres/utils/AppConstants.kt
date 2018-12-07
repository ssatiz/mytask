package com.influx.marcus.theatres.utils

import android.content.Context
import com.influx.marcus.theatres.BuildConfig
import com.influx.marcus.theatres.api.ApiInterface
import com.influx.marcus.theatres.api.pref.CinemaDetail
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern


/**
 * Created by Kavitha on 02-04-2018.
 */
class AppConstants {

    companion object Values {
        // Dont change Values here, to be done in app gradle based on build type
        val IS_DEBUG: Boolean = BuildConfig.IS_DEBUGMODE
        val APP_VERSION: String = BuildConfig.APP_VERSION
        val KEY_API_BASEURL: String = BuildConfig.SERVER_URL // this is for api
        val AUTHORISATION_HEADER = "Basic ${BuildConfig.AUTHORIZATION_HEADER}"
        val PRIVACY_URL = "https://dev.marcustheatres.com/privacy-policy"
        val FAQ_URL = "https://dev.marcustheatres.com/faq"
        val CONTACT_US = "https://dev.marcustheatres.com/contact-us"
        val ABOUT_US = "https://dev.marcustheatres.com/about-us"
        val TERMS_CONDITIONS = "https://dev.marcustheatres.com/terms-and-conditions"
        val APP_PLATFORM = "android"
        val KEY_DEVICENAME = "devicename"
        val KEY_USERID = "userid"
        val KEY_LOYALTYCARDNO = "cardno"
        val KEY_PROMOTIONS_DATA = "promotiondata"
        val KEY_MOVIECODE = "movieid"
        val KEY_TMDBID = "tmdbid"
        val KEY_SHOWTIME = "showtime"
        val KEY_THEATRESHOWTIME = "theatreshowtime"
        val KEY_THEATRESHOWTIMEOBJ = "theatreshowtimeobj"

        val KEY_STATE_CODE = "state"
        val KEY_FROM = "from"
        val KEY_BOOKINGFLOW = "bookingflow"
        val KEY_FIRSTNAME = "firstname"
        val KEY_LASTNAME = "lastname"
        val KEY_EMAIL = "email"
        val KEY_SOCIAL = "social"
        val KEY_SOCIALID = "socialid"
        val KEY_NONGPSFLOW = "gpsflow"
        val KEY_PREFEREDLANGUAGE = "preferedlanguages"
        val KEY_PREFEREDGENRE = "preferedgenre"
        val KEY_PREFEREDCINEMA = "preferedcinema"
        val KEY_PREFEREDCINEMALISTOBJ = "preferedcinemalistobj"
        val KEY_PROMOTIONS = "promotion"
        val KEY_SELECTEDCINEMA = "selectedcinema"
        val KEY_PREFEREDCINEMAPOS = "preferedcinemapos"
        val KEY_USERPREFERENCES = "userpreferences"
        val KEY_LATITUDE = "latitude"
        val KEY_LONGITUDE = "longitude"
        val KEY_FLOWFROMMODIFY = "flowfrommodify"
        val KEY_STATELIST = "statelist"
        val KEY_SEATSOBJECT = "seatsobject"
        val KEY_MOVIEPOSTER = "movieposter"
        val KEY_EXPIMAGE = "expimage"
        val KEY_SINGLEEXPIMAGE = "singleexpimage"
        val KEY_CINEMALOCATION = "cinemalocation"
        val KEY_MOVIENAME = "moviename"
        val KEY_DATE = "date"
        val KEY_TIME = "time"
        val KEY_CID = "cid"
        val KEY_SESSIONID = "sessionid"
        val KEY_SCREENNAME = "screenname"
        val KEY_SALEID = "saleid"
        val KEY_ISMMR = "mmr"
        val KEY_CCODE = "ccode"
        val KEY_SEATSTR = "seatstr"
        val KEY_BLOCKRESP = "blockresp"
        val KEY_ORDERID = "orderid"
        val KEY_FORGOTREWARDNO = "forgotrewardno"
        val KEY_EXPDATA: String = "expdata"
        val KEY_TIMESTR: String = "time_str"
        val KEY_MOVIETYPE = "movietype"
        val KEY_THEATREID = "theatreid"
        val KEY_MOVIEDETAILSOBJECT = "moviedetailobject"
        val KEY_CARDLIST = "reviewcardlist"
        val KEY_SEATDATA: String = "seatlayoutresp"
        val KEY_MYACCOUNT: String = "myaccount"
        val KEY_SPECIAL: String = "special"
        val KEY_UNRESERVEDBOOKING: String = "unreservedbooking"
        val KEY_OUTTHEATRES_OTGPS: String = "our_theatres_otgps"
        val KEY_OUTTHEATRES_OTNONGPS: String = "our_theatres_otnongps"
        val KEY_MOVIELIST: String = "our_movie_list"
        val KEY_BLOCKREQ: String = "blockRequestObject"
        val KEY_BOOKING_HISTORY: String = "bookingHistory"
        val KEY_UNRESERVE_BLOCKREQ: String = "unreservelockReq"
        val KEY_UNRESERVE_BLOCKRESP: String = "unreservelockResp"
        val KEY_CALLBLOCKSEAT: String = "callBlockSeatApi"
        val KEY_NEWUSER = "newuser"
        val KEY_THEATRELIST: String = "theatrelist"
        val KEY_THEATRECODE: String = "theatreCode"
        val KEY_THEATREIMAGE: String = "theatreImg"
        val KEY_THEATRENAME: String = "theatreName"
        val KEY_HOMEPAGEDATA = "homepageData"
        val KEY_CVV: String = "cvv"
        val KEY_ZIPCODE: String = "zipcode"
        val KEY_SHOWTIMERESOBJ: String = "showtimerespobj"
        val KEY_USERDATAOBJ: String = "userdetailobj"
        var KEY_FILTERDATE: String = "filterdateselection"
        var KEY_FILTERLANGUAGE: String = "filterlanguage"
        var KEY_FILTERGENRE: String = "filtergenre"
        var KEY_FILTERLOCDATE: String = "filterdate"
        var KEY_WEBVIEW_BASE_URL: String = "baseurl"
        var KEY_GIFT_CARD: String = "gift_card"
        var KEY_GUEST_USER: String = "guest_user"
        var KEY_FROM_SIDEMENU: String = "keysidemenu"
        val KEY_EXPERIENCELOGOURL = "explogourl"
        val KEY_EXPERIENCETEXT = "exptext"
        val magicalRewards = "movie-rewards"
        val privacypolicy = "privacy-policy"
        val faq = "faq"
        val contactus = "contact-us"
        val aboutus = "about-us"
        val termsandconditions = "terms-and-conditions"

        /**
         * To store data temporary before user save it to shared preferences
         */

        var cinemaList: ArrayList<String> = ArrayList()
        var localCinemadata: ArrayList<CinemaDetail> = ArrayList()
        var genreList: ArrayList<String> = ArrayList()
        var promotion: ArrayList<String> = ArrayList()
        var languageList: ArrayList<String> = ArrayList()
        var selectedCinmastoFilter: ArrayList<String> = ArrayList()
        var selectedGenrestoFilter: ArrayList<String> = ArrayList()
        var selectedLanguagestoFilter: ArrayList<String> = ArrayList()
        var is_filterApplied = false
        var isSplash = false
        var isFromNavDraw = false
        var state = ""
        var longitude: String = ""
        var latitute: String = ""
        var cinemaclicked: String = ""
        var isFromMyAccount: Boolean = false
        var isFromPayment = false
        val KEY_MMRRESP = "mmrresp"


        fun putString(key: String, value: String, context: Context): Unit {
            val db: TinyDB = TinyDB(context)
            db.putString(key, value)

        }

        fun getString(key: String, context: Context): String {
            val db: TinyDB = TinyDB(context)
            return db.getString(key)
        }

        fun getBoolean(key: String, context: Context): Boolean {
            return TinyDB(context).getBoolean(key)
        }

        fun putBoolean(key: String, value: Boolean, context: Context) {
            TinyDB(context).putBoolean(key, value)
        }

        fun putStringList(key: String, value: ArrayList<String>, context: Context) {
            TinyDB(context).putListString(key, value)
        }

        fun getStringList(key: String, context: Context): ArrayList<String> {
            return TinyDB(context).getListString(key)
        }

        fun putObject(key: String, value: Any, context: Context) {
            TinyDB(context).putObject(key, value)
        }

        fun getObject(key: String, context: Context, classOfAny: Class<Any>): Any? {
            try {
                val data = TinyDB(context).getObject(key, classOfAny)
                return data
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }

        fun putObjectList(key: String, value: ArrayList<Any>, context: Context) {
            TinyDB(context).putListObject(key, value)
        }

        fun getObjectList(key: String, context: Context, classOfAny: Class<Any>): Any {
            return TinyDB(context).getListObject(key, classOfAny)
        }

        fun addToPreferedLanguage(lang: String, context: Context) {
            val listOfPrefLang: ArrayList<String> = getStringList(AppConstants.KEY_PREFEREDLANGUAGE,
                    context)
            var isLanguagePresent = false
            for (eachLang in listOfPrefLang) {
                if (eachLang.equals(lang)) {
                    isLanguagePresent = true
                }
            }
            if (!isLanguagePresent) {
                listOfPrefLang.add(lang)
            }
            putStringList(AppConstants.KEY_PREFEREDLANGUAGE, listOfPrefLang, context)
        }

        fun removeFromPreferedLanguage(lang: String, context: Context) {
            val listOfPrefLang: ArrayList<String> = getStringList(AppConstants.KEY_PREFEREDLANGUAGE,
                    context)
            var isLanguagePresent = false
            for (eachLang in listOfPrefLang) {
                if (eachLang.equals(lang)) {
                    isLanguagePresent = true
                }
            }
            if (isLanguagePresent) {
                listOfPrefLang.remove(lang)
            }// do nothing if not present
            putStringList(AppConstants.KEY_PREFEREDLANGUAGE, listOfPrefLang, context)
        }


        fun addToPreferedGenre(genre: String, context: Context) {
            val listOfPrefGenre: ArrayList<String> = getStringList(AppConstants.KEY_PREFEREDGENRE,
                    context)
            var isGenrePresent = false
            for (eachGenre in listOfPrefGenre) {
                if (eachGenre.equals(genre)) {
                    isGenrePresent = true
                }
            }
            if (!isGenrePresent) {
                listOfPrefGenre.add(genre)
            }
            putStringList(AppConstants.KEY_PREFEREDGENRE, listOfPrefGenre, context)
        }

        fun removeFromPreferedGenre(genre: String, context: Context) {
            val listOfPrefGenre: ArrayList<String> = getStringList(AppConstants.KEY_PREFEREDGENRE,
                    context)
            var isGenrePresent = false
            for (eachGenre in listOfPrefGenre) {
                if (eachGenre.equals(genre)) {
                    isGenrePresent = true
                }
            }
            if (isGenrePresent) {
                listOfPrefGenre.remove(genre)
            }// do nothing if not present
            putStringList(AppConstants.KEY_PREFEREDGENRE, listOfPrefGenre, context)
        }

        fun addToPreferedCinema(cinemaId: String, context: Context) {
            val listOfPrefCinema: ArrayList<String> = getStringList(AppConstants.KEY_PREFEREDCINEMA,
                    context)
            var isCinemaPresent = false
            for (eachLang in listOfPrefCinema) {
                if (eachLang.equals(cinemaId)) {
                    isCinemaPresent = true
                }
            }
            if (!isCinemaPresent) {
                listOfPrefCinema.add(cinemaId)
            }// do nothing if alreadypresent
            putStringList(AppConstants.KEY_PREFEREDCINEMA, listOfPrefCinema, context)
        }

        fun addpreferedcinemapos(parentpos: Int, context: Context) {
            val listOfPrefCinema: ArrayList<String> = getStringList(AppConstants.KEY_PREFEREDCINEMAPOS,
                    context)
            listOfPrefCinema.add(parentpos.toString())
            putStringList(AppConstants.KEY_PREFEREDCINEMAPOS, listOfPrefCinema, context)
        }

        fun removepreferedcinemapos(parentpos: Int, context: Context) {
            val listOfPrefCinema: ArrayList<String> = getStringList(AppConstants.KEY_PREFEREDCINEMAPOS,
                    context)
            listOfPrefCinema.remove(parentpos.toString())
            putStringList(AppConstants.KEY_PREFEREDCINEMAPOS, listOfPrefCinema, context)
        }

        fun removeFromPreferedCinema(cinemaId: String, context: Context) {
            val listOfPrefCinema: ArrayList<String> = getStringList(AppConstants.KEY_PREFEREDCINEMA,
                    context)
            var isCinemaPresent = false
            for (eachLang in listOfPrefCinema) {
                if (eachLang.equals(cinemaId)) {
                    isCinemaPresent = true
                }
            }
            if (isCinemaPresent) {
                listOfPrefCinema.remove(cinemaId)
            }// do nothing if not present
            putStringList(AppConstants.KEY_PREFEREDCINEMA, listOfPrefCinema, context)
        }

        fun clearpreferedlist(string: String, context: Context) {
            val listOfPref: ArrayList<String> = getStringList(string, context)
            listOfPref.clear()
            putStringList(string, listOfPref, context)
            LogUtils.d(string, listOfPref.size.toString())
        }


        fun extractYTId(ytUrl: String): String {
            var vId: String = "error"
            val pattern = Pattern.compile(
                    "^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$",
                    Pattern.CASE_INSENSITIVE)
            val matcher = pattern.matcher(ytUrl)
            if (matcher.matches()) {
                vId = matcher.group(1)
            }
            if (vId.equals("error")) {
                val youtubeId = getYouTubeId(ytUrl)
                return youtubeId
            }
            return vId
        }


        fun getYouTubeId(youTubeUrl: String): String {
            val pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*"
            val compiledPattern = Pattern.compile(pattern)
            val matcher = compiledPattern.matcher(youTubeUrl)
            return if (matcher.find()) {
                matcher.group()
            } else {
                "error"
            }
        }

        fun theMonth(month: Int): String {
            val monthNames = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
            return monthNames[month - 1]
        }
    }
}