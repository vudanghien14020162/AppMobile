package com.mobitv.ott.other;

import android.app.Application;
import android.util.Log;

import androidx.annotation.Nullable;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import okhttp3.OkHttpClient;

public class OnTVApp extends Application {
    private static final String TAG = "AVG";

    @Override
    public void onCreate() {
//		MultiDex.install(getApplicationContext());
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }

}