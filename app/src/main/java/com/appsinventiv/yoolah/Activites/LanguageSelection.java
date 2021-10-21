package com.appsinventiv.yoolah.Activites;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.appsinventiv.yoolah.Activites.UserManagement.LoginUser;
import com.appsinventiv.yoolah.R;
import com.appsinventiv.yoolah.Utils.CommonUtils;
import com.appsinventiv.yoolah.Utils.SharedPrefs;

import java.util.Locale;

public class LanguageSelection extends AppCompatActivity {
    RadioButton english, german;
    private String languageChosen;
    Button update;

    RelativeLayout englishLanguage, germanLanguage;
    ImageView imgEnglish, imgGerman;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selection);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        this.setTitle(getResources().getString(R.string.choose_app_language));
        englishLanguage = findViewById(R.id.englishLanguage);
        germanLanguage = findViewById(R.id.germanLanguage);
        imgEnglish = findViewById(R.id.imgEnglish);
        imgGerman = findViewById(R.id.imgGerman);

        if (SharedPrefs.getAppLanguage().equalsIgnoreCase("german")) {
            imgGerman.setVisibility(View.VISIBLE);
            imgEnglish.setVisibility(View.GONE);
        } else {
            imgEnglish.setVisibility(View.VISIBLE);
            imgGerman.setVisibility(View.GONE);

        }


        englishLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgEnglish.setVisibility(View.VISIBLE);
                imgGerman.setVisibility(View.GONE);
                languageChosen = "english";
                String languageToLoad = "en"; // your language
                Locale locale = new Locale(languageToLoad);
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getBaseContext().getResources().updateConfiguration(config,
                        getBaseContext().getResources().getDisplayMetrics());
                SharedPrefs.setAppLanguage("english");

            }
        });
        germanLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgEnglish.setVisibility(View.GONE);
                imgGerman.setVisibility(View.VISIBLE);
                languageChosen = "German";
                String languageToLoad = "de"; // your language
                Locale locale = new Locale(languageToLoad);
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getBaseContext().getResources().updateConfiguration(config,
                        getBaseContext().getResources().getDisplayMetrics());
                SharedPrefs.setAppLanguage("german");
            }
        });


        imgEnglish = findViewById(R.id.imgEnglish);
        update = findViewById(R.id.update);
        imgGerman = findViewById(R.id.imgGerman);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LanguageSelection.this, LanguageSelection.class));
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {


            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
