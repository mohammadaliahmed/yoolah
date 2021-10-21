package com.appsinventiv.yoolah.Activites;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.appsinventiv.yoolah.Activites.UserManagement.LoginUser;
import com.appsinventiv.yoolah.Activites.UserManagement.MyProfile;
import com.appsinventiv.yoolah.R;
import com.appsinventiv.yoolah.Utils.AppConfig;
import com.appsinventiv.yoolah.Utils.SharedPrefs;
import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

public class Settings extends AppCompatActivity {

    TextView name, email;
    CircleImageView picture;
    RelativeLayout viewProfile;
    RelativeLayout chooseAppLangaue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        this.setTitle(getResources().getString(R.string.settings));
        chooseAppLangaue = findViewById(R.id.chooseAppLangaue);
        viewProfile = findViewById(R.id.viewProfile);
        picture = findViewById(R.id.picture);
        email = findViewById(R.id.email);
        name = findViewById(R.id.name);
        Glide.with(this).load(AppConfig.BASE_URL_Image+SharedPrefs.getUserModel().getPicUrl()).placeholder(R.drawable.ic_profile_plc).into(picture);
        name.setText(SharedPrefs.getUserModel().getName());
        email.setText(SharedPrefs.getUserModel().getEmail());


        chooseAppLangaue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.this, LanguageSelection.class));
            }
        });
        viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.this, MyProfile.class));
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
