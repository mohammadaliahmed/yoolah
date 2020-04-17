package com.appsinventiv.yoolah.Utils;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import com.google.firebase.FirebaseApp;


/**
 * Created by AliAh on 11/04/2018.
 */

public class ApplicationClass extends Application {
    private static ApplicationClass instance;



    public static ApplicationClass getInstance() {
        return instance;
    }



    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);

        instance = this;
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }


}
