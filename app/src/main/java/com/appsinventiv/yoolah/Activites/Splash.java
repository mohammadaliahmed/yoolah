package com.appsinventiv.yoolah.Activites;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.appsinventiv.yoolah.Activites.UserManagement.LoginActivity;
import com.appsinventiv.yoolah.Activites.UserManagement.LoginUser;
import com.appsinventiv.yoolah.R;
import com.appsinventiv.yoolah.Utils.CommonUtils;
import com.appsinventiv.yoolah.Utils.SharedPrefs;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class
Splash extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 500;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        if (SharedPrefs.getAppLanguage().equalsIgnoreCase("german")) {
            String languageToLoad = "de"; // your language
            Locale locale = new Locale(languageToLoad);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
        } else {
            String languageToLoad = "en"; // your language
            Locale locale = new Locale(languageToLoad);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
        }


        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                if (SharedPrefs.getUserModel() != null) {
                    Intent i = new Intent(Splash.this, MainActivity.class);
                    startActivity(i);
                } else {
                    Intent i = new Intent(Splash.this, LoginUser.class);
                    startActivity(i);
                }

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
        /*
         * alias: medipandarider
         * key: medipandarider
         * pass: medipandarider
         * */

    }


}
