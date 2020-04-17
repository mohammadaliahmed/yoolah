package com.appsinventiv.yoolah.Activites;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;


import com.appsinventiv.yoolah.R;

import androidx.appcompat.app.AppCompatActivity;

public class PlayVideo extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);

        String LINK = getIntent().getStringExtra("videoUrl");
        VideoView videoView = (VideoView) findViewById(R.id.video);
        MediaController mc = new MediaController(this);
        mc.setAnchorView(videoView);
        mc.setMediaPlayer(videoView);
        Uri video = Uri.parse(LINK);
        videoView.setMediaController(mc);
        videoView.setVideoURI(video);
        videoView.start();


    }


}
