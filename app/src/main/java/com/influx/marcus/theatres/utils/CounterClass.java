package com.influx.marcus.theatres.utils;


import android.os.CountDownTimer;

import java.util.concurrent.TimeUnit;

/**
 * Created by hardik on 15/06/17.
 */
public class CounterClass extends CountDownTimer {

    private static String hms;
    private static CounterClass instance;
    private static CountdownTick tickCallback;

    private CounterClass(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    public static void setListener(CountdownTick mCallback) {
        tickCallback = mCallback;
    }

    public static CounterClass initInstance(long millisInFuture, long countDownInterval) {
        if (instance == null) {
            instance = new CounterClass(millisInFuture, countDownInterval);
        }
        return instance;
    }

    public static CounterClass getInstance() throws Exception {
        if (instance == null) {
            return instance;
        } else {
            return instance;
        }
    }

    public static String getFormatedTime() {
        return hms;
    }

    @Override
    public void onTick(long l) {
        long millis = l;
        hms = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

        if (tickCallback != null) tickCallback.onTick(l / 1000);
    }

    @Override
    public void onFinish() {
        if (tickCallback != null) tickCallback.onTick(-1);
        instance.cancel();
    }

    public interface CountdownTick {
        void onTick(long secondsLeft);
    }

}

