package com.appsinventiv.yoolah.Utils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppConfig {
        public static String LPTOP_ID = "http://172.17.0.53/yoolahserver/public/";
//    public static String LPTOP_ID = "http://192.168.8.104/yoolahserver/";
    public static String SERVER_URL = "http://yoolah.com/";
    public static String BASE_URL = SERVER_URL;
    public static String API_USERNAME = "WF9.FJ8u'FP{c5Pw";
    public static String API_PASSOWRD = "3B~fauh5s93j[FKb";

    public static String BASE_URL_Image = BASE_URL + "images/";
    public static String BASE_URL_AUDIO = BASE_URL + "audio/";
    public static String BASE_URL_Videos = BASE_URL + "videos/";
    public static String BASE_URL_Documents = BASE_URL + "document/";
    public static String BASE_URL_QR = BASE_URL + "qr/";
    public static String TOKKEN = "http://acnure.com/";

    public static OkHttpClient configureTimeouts() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(90, TimeUnit.SECONDS) // Set your timeout duration here.
                .writeTimeout(90, TimeUnit.SECONDS)
                .readTimeout(90, TimeUnit.SECONDS)
                .build();
        return okHttpClient;
    }

    public static Retrofit getRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(AppConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(configureTimeouts())
                .build();
    }

}
