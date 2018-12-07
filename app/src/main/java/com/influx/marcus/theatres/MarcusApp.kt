package com.influx.marcus.theatres

import android.os.StrictMode
import android.support.multidex.MultiDexApplication
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.facebook.stetho.Stetho
import com.influx.marcus.theatres.utils.AppConstants
import com.jaredrummler.android.device.DeviceName


class MarcusApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(this);
        Stetho.initializeWithDefaults(this)
        val deviceName = DeviceName.getDeviceName()
        AppConstants.putString(AppConstants.KEY_DEVICENAME, deviceName, this)
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        FacebookSdk.sdkInitialize(getApplicationContext())
        AppEventsLogger.activateApp(this)
    }
}