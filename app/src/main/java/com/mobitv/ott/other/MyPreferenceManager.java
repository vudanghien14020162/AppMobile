package com.mobitv.ott.other;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;

/**
 * Created by sonth on 11/11/2016.
 */

public class MyPreferenceManager {
    private static MyPreferenceManager INSTANCE = null;
    private SharedPreferences mPrefs;

    private MyPreferenceManager(Context context) {
        mPrefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    private synchronized static void createInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new MyPreferenceManager(context);
        }
    }

    public static MyPreferenceManager getInstance(Context context) {
        if (INSTANCE == null) createInstance(context);
        return INSTANCE;
    }

    public void setValue(String key, String value) {
        mPrefs.edit().putString(key, value)
                .apply();
    }
    public String getValue(String key, String defaultValue) {
        return mPrefs.getString(key, defaultValue);
    }

    public void setValue(String key, int value) {
        mPrefs.edit().putInt(key, value)
                .apply();
    }
    public int getValue(String key, int defaultValue) {
        return mPrefs.getInt(key, defaultValue);
    }

    public boolean isLogIn(){
        return mPrefs.getBoolean(Const.IS_LOG_IN, false);
    }
    public void setLogIn(boolean isLogIn){
        mPrefs.edit().putBoolean(Const.IS_LOG_IN, isLogIn).apply();
    }
}
