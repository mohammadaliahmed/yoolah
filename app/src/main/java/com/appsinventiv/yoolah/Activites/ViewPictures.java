package com.appsinventiv.yoolah.Activites;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.appsinventiv.yoolah.R;
import com.appsinventiv.yoolah.Utils.AppConfig;
import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;

import androidx.appcompat.app.AppCompatActivity;

public class ViewPictures extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pictures);
        Intent i = getIntent();
        String url = i.getStringExtra("url");
        ImageView img = findViewById(R.id.img);
        if (url.contains("/storage")) {
            Glide.with(this).load(url).into(img);


        } else {
            Glide.with(this).load(AppConfig.BASE_URL_Image + url).into(img);

        }
        img.setOnTouchListener(new ImageMatrixTouchHandler(this));
    }
}
