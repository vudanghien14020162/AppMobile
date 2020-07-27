package com.mobitv.ott.pojo;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.mobitv.ott.BuildConfig;
import com.mobitv.ott.activity.StartActivity;
import com.mobitv.ott.other.Const;
import com.mobitv.ott.other.MyPreferenceManager;
import com.mobitv.ott.other.Utils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.mobitv.ott.BuildConfig.BASE_URL;

public class  APIClient {
    private static final String TAG="APIClient";
    public static APIClient getInstance(Context context) {
        return new APIClient(context);
    }

    public static APIClient getInstanceAccess(Context context) {
        return new APIClient(context, true);
    }

    private Retrofit retrofit;

    private APIClient(Context context) {
        MyPreferenceManager myPreferenceManager = MyPreferenceManager.getInstance(context);
        final String guid = myPreferenceManager.getValue(Const.GUID, "");
        final String userID = myPreferenceManager.getValue(Const.USER_ID, "");
        String serverKey = myPreferenceManager.getValue(Const.SERVER_KEY, "");
        String date = Utils.getSendTime();
        final String mbCode = Utils.md5(guid + "$" + userID + "$" + date + "$" + serverKey);
        final String token = myPreferenceManager.getValue(Const.AUTH, "");
        // Log.d(TAG,"TOKEN: "+token+" BASE_URL"+BASE_URL);
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(@NonNull Interceptor.Chain chain) throws IOException {
                Request original = chain.request();

                Request request = original.newBuilder()
                        .header("User-Agent", "AndroidOS" + ":" + Build.VERSION.SDK_INT + ":" + BuildConfig.VERSION_CODE)
                        .addHeader(Const.GUID, guid)
                        .addHeader(Const.USER_ID, userID)
                        .addHeader(Const.MBCODE, mbCode)
                        .addHeader(Const.AUTH, token)
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });
        OkHttpClient client = clientBuilder.build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    private APIClient(Context context, boolean isAccess) {
        MyPreferenceManager myPreferenceManager = MyPreferenceManager.getInstance(context);
        final String guid = myPreferenceManager.getValue(Const.GUID, "");
        final String userID = myPreferenceManager.getValue(Const.USER_ID, "");
        String serverKey = myPreferenceManager.getValue(Const.SERVER_KEY, "");
        String date = Utils.getSendTime();
        final String mbCode = Utils.md5(guid + "$" + userID + "$" + date + "$" + serverKey);
        final String token = myPreferenceManager.getValue(Const.AUTH, "");

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(@NonNull Interceptor.Chain chain) throws IOException {
                Request original = chain.request();

                Request request = original.newBuilder()
                        .header("User-Agent", "AndroidOS" + ":" + Build.VERSION.SDK_INT + ":" + BuildConfig.VERSION_CODE)
                        .addHeader(Const.GUID, guid)
                        .addHeader(Const.USER_ID, userID)
                        .addHeader(Const.MBCODE, mbCode)
                        .addHeader(Const.AUTH, token)
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });
        OkHttpClient client = clientBuilder.build();
        String baseURL = "http://ads.liforte.com";
        retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    public Retrofit getClient() {
        return retrofit;
    }
}
